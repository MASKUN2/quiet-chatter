package maskun.quietchatter.shared.security;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class JwtAuthFilterTest {

    @BeforeEach
    void setUp() {
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(null, null));
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();

        SecurityContextHolder.clearContext();
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void NoAuthenticated()throws ServletException, IOException {
        JwtAuthFilter jwtAuthFilter = new JwtAuthFilter(mock(JwtAuthManager.class));

        FilterChain mockfilterChain = mock(FilterChain.class);

        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        when(mockRequest.getCookies()).thenReturn(new Cookie[]{});

        jwtAuthFilter.doFilterInternal(mockRequest, mock(HttpServletResponse.class), mockfilterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(mockfilterChain).doFilter(Mockito.eq(mockRequest), any());
    }

    @Test
    void Authenticated() throws ServletException, IOException {
        JwtAuthManager mockJwtAuthManager = mock(JwtAuthManager.class);
        when(mockJwtAuthManager.parse(Mockito.eq("some value"))).thenReturn(mock(Authentication.class));

        JwtAuthFilter jwtAuthFilter = new JwtAuthFilter(mockJwtAuthManager);
        FilterChain mockfilterChain = mock(FilterChain.class);

        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        when(mockRequest.getCookies()).thenReturn(new Cookie[]{new Cookie("access_token", "some value")});

        jwtAuthFilter.doFilterInternal(mockRequest, mock(HttpServletResponse.class), mockfilterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();

        verify(mockfilterChain).doFilter(Mockito.eq(mockRequest), any());
    }
}
