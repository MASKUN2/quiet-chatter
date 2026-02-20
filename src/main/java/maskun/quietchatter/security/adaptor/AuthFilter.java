package maskun.quietchatter.security.adaptor;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import maskun.quietchatter.security.application.in.AuthMemberService;
import maskun.quietchatter.security.domain.AuthMember;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@NullMarked
class AuthFilter extends OncePerRequestFilter {
    private final AuthTokenService authTokenService;
    private final AuthMemberService authMemberService;

    AuthFilter(AuthTokenService authTokenService, AuthMemberService authMemberService) {
        this.authTokenService = authTokenService;
        this.authMemberService = authMemberService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        Authentication auth = attempt(request, response);

        if (auth != null) {
            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        filterChain.doFilter(request, response);
    }

    private @Nullable Authentication attempt(HttpServletRequest request, HttpServletResponse response) {
        String accessToken = authTokenService.extractAccessToken(request);
        if (accessToken != null) {
            try {
                UUID memberId = authTokenService.parseAccessTokenAndGetMemberId(accessToken);
                AuthMember authMember = authMemberService.findOrThrow(memberId);
                return new AuthMemberToken(authMember);
            } catch (ExpiredAuthTokenException e) {
                // Access token expired, try refresh token
            } catch (AuthTokenException e) {
                return null;
            }
        }

        return tryWithRefreshToken(request, response);
    }

    private @Nullable Authentication tryWithRefreshToken(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = authTokenService.extractRefreshToken(request);
        if (refreshToken == null) {
            return null;
        }

        try {
            String tokenId = authTokenService.parseRefreshTokenAndGetTokenId(refreshToken);
            UUID memberId = authTokenService.findMemberIdByRefreshTokenIdOrThrow(tokenId);
            AuthMember authMember = authMemberService.findOrThrow(memberId);
            AuthMemberToken auth = new AuthMemberToken(authMember);

            String newAccessToken = authTokenService.createNewAccessToken(memberId);
            String newRefreshToken = authTokenService.createAndSaveRefreshToken(memberId);

            authTokenService.putAccessToken(response, newAccessToken);
            authTokenService.putRefreshToken(response, newRefreshToken);

            authTokenService.deleteRefreshTokenById(tokenId); // delete old
            return auth;
        } catch (Exception e) {
            // If refresh token is invalid or expired, or member not found, return null
            return null;
        }
    }
}
