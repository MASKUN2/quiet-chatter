package maskun.quietchatter.security.adaptor;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import maskun.quietchatter.security.application.in.AuthMemberService;
import maskun.quietchatter.security.domain.AuthMember;
import org.jspecify.annotations.NullMarked;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher.Builder;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Slf4j
@NullMarked
class AnonymousToGuestPromotionFilter extends OncePerRequestFilter {
    static final List<RequestMatcher> requestMatchers;
    private final AuthTokenService authTokenService;
    private final AuthMemberService authMemberService;

    static {
        Builder builder = PathPatternRequestMatcher.withDefaults();
        requestMatchers = List.of(
                builder.matcher(HttpMethod.POST, "/api/v1/talks"),
                builder.matcher(HttpMethod.GET, "/api/v1/talks"),
                builder.matcher(HttpMethod.POST, "/api/v1/reactions"),
                builder.matcher(HttpMethod.DELETE, "/api/v1/reactions")
        );
    }

    AnonymousToGuestPromotionFilter(AuthTokenService authTokenService, AuthMemberService authMemberService) {
        this.authTokenService = authTokenService;
        this.authMemberService = authMemberService;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        if (!isRequestMatch(request)) {
            return true; // when not matched, pass
        }

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

    private static boolean isRequestMatch(HttpServletRequest request) {
        for (RequestMatcher matcher : requestMatchers) {
            if (matcher.matches(request)) {
                return true;
            }
        }
        return false;
    }
}
