package maskun.quietchatter.security.internal;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NullMarked;
import org.slf4j.MDC;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@NullMarked
@Slf4j
class MdcFilter extends OncePerRequestFilter {

    private static final String TRACE_ID = "trace-id";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String traceId = UUID.randomUUID().toString();
        MDC.put(TRACE_ID, traceId);

        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Object principal = (authentication != null) ? authentication.getPrincipal() : "anonymous";

            log.info("Request Start - TraceID: {}, URI: {}, IP: {}, Principal: {}",
                    traceId, request.getRequestURI(), request.getRemoteAddr(), principal);

            filterChain.doFilter(request, response);
        } catch (Exception e) {
            log.error("Request Error - TraceID: {}, Message: {}", traceId, e.getMessage(), e);
            throw e;
        } finally {
            MDC.clear();
        }
    }
}
