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

        // Nginx가 전달한 X-Request-ID가 있으면 사용, 없으면 새로 생성
        String traceId = request.getHeader("X-Request-ID");
        if (traceId == null || traceId.isEmpty()) {
            traceId = UUID.randomUUID().toString();
        }

        MDC.put(TRACE_ID, traceId);

        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Object principal = (authentication != null) ? authentication.getPrincipal() : "anonymous";

            // Nginx 등 프록시를 거칠 경우 실제 클라이언트 IP를 가져옴
            String clientIp = getClientIp(request);

            String fullUri = getFullUri(request);

            log.info("Request Start - TraceID: {}, Method: {}, URI: {}, IP: {}, Principal: {}",
                    traceId, request.getMethod(), fullUri, clientIp, principal);

            filterChain.doFilter(request, response);
        } catch (Exception e) {
            log.error("Request Error - TraceID: {}, Message: {}", traceId, e.getMessage(), e);
            throw e;
        } finally {
            MDC.clear();
        }
    }

    private String getFullUri(HttpServletRequest request) {
        String uri = request.getRequestURI();
        String queryString = request.getQueryString();

        if (queryString == null || queryString.isEmpty()) {
            return uri;
        }

        return uri + "?" + queryString;
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        if (ip != null && ip.contains(",")) {
            return ip.split(",")[0].trim();
        }
        return ip;
    }
}
