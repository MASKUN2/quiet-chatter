package maskun.quietchatter.security.internal;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import maskun.quietchatter.security.AuthMember;
import maskun.quietchatter.security.AuthMemberToken;
import maskun.quietchatter.security.AuthTokenException;
import maskun.quietchatter.security.ExpiredAuthTokenException;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

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
        if (accessToken == null) {
            return null;
        }

        try {
            UUID memberId = authTokenService.parseAccessTokenAndGetMemberId(accessToken);
            AuthMember authMember = authMemberService.findOrThrow(memberId);
            return new AuthMemberToken(authMember);
        } catch (ExpiredAuthTokenException e) {
            return tryWithRefreshToken(request, response);
        } catch (AuthTokenException e) {
            return null;
        }
    }

    private @Nullable Authentication tryWithRefreshToken(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = authTokenService.extractRefreshToken(request);
        if (refreshToken == null) {
            return null;
        }

        String tokenId = authTokenService.parseRefreshTokenAndGetTokenId(refreshToken);
        UUID memberId = authTokenService.findMemberIdByRefreshTokenIdOrThrow(tokenId);
        AuthMemberToken auth = new AuthMemberToken(authMemberService.findOrThrow(memberId));

        String newAccessToken = authTokenService.createNewAccessToken(memberId);
        String newRefreshToken = authTokenService.createAndSaveRefreshToken(memberId);
        authTokenService.putAccessToken(response, newAccessToken);
        authTokenService.putRefreshToken(response, newRefreshToken);

        authTokenService.deleteRefreshTokenById(tokenId); // delete old
        return auth;
    }
}
