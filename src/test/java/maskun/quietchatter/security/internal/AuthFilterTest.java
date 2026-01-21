package maskun.quietchatter.security.internal;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import maskun.quietchatter.security.ExpiredAuthTokenException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

class AuthFilterTest {

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
    void NoAccessTokenAndNoAuthenticated() throws ServletException, IOException {
        AuthTokenService mockAuthTokenService = mock(AuthTokenService.class);
        when(mockAuthTokenService.extractAccessToken(any())).thenReturn(null);
        AuthFilter authFilter = new AuthFilter(mockAuthTokenService, mock(AuthMemberService.class));

        FilterChain mockfilterChain = mock(FilterChain.class);

        authFilter.doFilterInternal(mock(HttpServletRequest.class), mock(HttpServletResponse.class), mockfilterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(mockfilterChain).doFilter(any(), any());
    }

    @Test
    void ExpiredAccessTokenNoRefreshTokenAndNoAuthenticated() throws ServletException, IOException {
        AuthTokenService mockAuthTokenService = mock(AuthTokenService.class);
        when(mockAuthTokenService.extractAccessToken(any())).thenReturn("some expired token");
        when(mockAuthTokenService.parseAccessTokenAndGetMemberId(any())).thenThrow(
                new ExpiredAuthTokenException("some expired token"));
        when(mockAuthTokenService.extractRefreshToken(any())).thenReturn(null);

        AuthFilter authFilter = new AuthFilter(mockAuthTokenService, mock(AuthMemberService.class));

        FilterChain mockfilterChain = mock(FilterChain.class);

        authFilter.doFilterInternal(mock(HttpServletRequest.class), mock(HttpServletResponse.class), mockfilterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(mockfilterChain).doFilter(any(), any());
    }

    @Test
    void ExpiredAccessTokenAndExpiredRefreshTokenThenThrow() throws ServletException, IOException {
        AuthTokenService mockAuthTokenService = mock(AuthTokenService.class);
        when(mockAuthTokenService.extractAccessToken(any())).thenReturn("some expired token");
        when(mockAuthTokenService.parseAccessTokenAndGetMemberId(any())).thenThrow(
                new ExpiredAuthTokenException("some expired token"));
        when(mockAuthTokenService.extractRefreshToken(any())).thenReturn("some expired refresh token");
        when(mockAuthTokenService.parseRefreshTokenAndGetTokenId(any())).thenThrow(
                new ExpiredAuthTokenException("some expired refresh token"));

        AuthFilter authFilter = new AuthFilter(mockAuthTokenService, mock(AuthMemberService.class));

        FilterChain mockfilterChain = mock(FilterChain.class);

        assertThatThrownBy(() -> authFilter.doFilterInternal(
                mock(HttpServletRequest.class), mock(HttpServletResponse.class), mockfilterChain)
        ).isInstanceOf(ExpiredAuthTokenException.class);
    }
}
