package maskun.quietchatter.security.adaptor;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import maskun.quietchatter.security.application.in.AuthMemberService;
import maskun.quietchatter.security.domain.AuthMember;
import org.jspecify.annotations.NullMarked;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@NullMarked
class AnonymousToGuestPromotionFilter extends OncePerRequestFilter {
    private final AuthTokenService authTokenService;
    private final AuthMemberService authMemberService;

    AnonymousToGuestPromotionFilter(AuthTokenService authTokenService, AuthMemberService authMemberService) {
        this.authTokenService = authTokenService;
        this.authMemberService = authMemberService;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return !(auth instanceof AnonymousAuthenticationToken); // when not Anonymous, pass
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        AuthMember newGuest = authMemberService.createNewGuest();
        AuthMemberToken auth = new AuthMemberToken(newGuest);
        SecurityContextHolder.getContext().setAuthentication(auth);

        String newAccessToken = authTokenService.createNewAccessToken(newGuest.id());
        String newRefreshToken = authTokenService.createAndSaveRefreshToken(newGuest.id());
        authTokenService.putAccessToken(response, newAccessToken);
        authTokenService.putRefreshToken(response, newRefreshToken);

        filterChain.doFilter(request, response);
    }

}
