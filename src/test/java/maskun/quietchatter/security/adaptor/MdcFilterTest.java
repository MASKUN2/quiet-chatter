package maskun.quietchatter.security.adaptor;

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

class MdcFilterTest {

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
