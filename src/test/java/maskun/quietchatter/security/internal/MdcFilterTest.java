package maskun.quietchatter.security.internal;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MdcFilterTest {

    @Test
    void shouldSanitizeSensitiveQueryParameters() {
        // given
        MdcFilter mdcFilter = new MdcFilter();
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/api/test");
        when(request.getQueryString()).thenReturn("id=1&password=1234&token=abcde&secret=xyz&key=999&other=value");

        // when
        String fullUri = mdcFilter.getFullUri(request);

        // then
        assertThat(fullUri).contains("id=1");
        assertThat(fullUri).contains("password=****");
        assertThat(fullUri).contains("token=****");
        assertThat(fullUri).contains("secret=****");
        assertThat(fullUri).contains("key=****");
        assertThat(fullUri).contains("other=value");
    }

    @Test
    void mdcIsSetAndCleared() throws ServletException, IOException {
        MdcFilter mdcFilter = new MdcFilter();
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain filterChain = (req, res) -> {
            assertThat(MDC.get("trace-id")).isNotNull();
        };

        mdcFilter.doFilterInternal(request, response, filterChain);

        assertThat(MDC.get("trace-id")).isNull();
    }

    @Test
    void mdcIsClearedEvenOnException() throws ServletException, IOException {
        MdcFilter mdcFilter = new MdcFilter();
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);
        doThrow(new RuntimeException("test exception")).when(filterChain).doFilter(any(), any());

        assertThatThrownBy(() -> mdcFilter.doFilterInternal(request, response, filterChain))
                .isInstanceOf(RuntimeException.class);

        assertThat(MDC.get("trace-id")).isNull();
    }
}
