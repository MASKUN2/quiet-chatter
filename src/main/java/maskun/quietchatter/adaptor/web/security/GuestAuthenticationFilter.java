package maskun.quietchatter.adaptor.web.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@RequiredArgsConstructor
public class GuestAuthenticationFilter extends OncePerRequestFilter {
    private final AuthenticationProvider authenticationProvider;
    private final List<RequestMatcher> requestMatchers;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        if (!isTarget(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        Authentication auth = authenticationProvider.getGuest();
        SecurityContextHolder.getContext().setAuthentication(auth);

        filterChain.doFilter(request, response);
    }

    private boolean isTarget(HttpServletRequest request) {
        if (!isMatch(request)) {
            return false;
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return isPromotable(auth);
    }

    private boolean isMatch(HttpServletRequest request) {
        return requestMatchers.stream()
                .anyMatch(matcher -> matcher.matches(request));
    }

    private static boolean isPromotable(Authentication auth) {
        return auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken;
    }
}
