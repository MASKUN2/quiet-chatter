package maskun.quietchatter.security.adaptor;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import maskun.quietchatter.member.domain.Role;
import maskun.quietchatter.security.application.in.AuthMemberService;
import maskun.quietchatter.security.domain.AuthMember;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthFilterTest {

    @BeforeEach
    void setUp() {
        // Ensure context is clear before test
        SecurityContextHolder.clearContext();
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
        FilterChain mockFilterChain = mock(FilterChain.class);

        authFilter.doFilterInternal(mock(HttpServletRequest.class), mock(HttpServletResponse.class), mockFilterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(mockFilterChain).doFilter(any(), any());
    }

    @Test
    void ExpiredAccessTokenNoRefreshTokenAndNoAuthenticated() throws ServletException, IOException {
        AuthTokenService mockAuthTokenService = mock(AuthTokenService.class);
        when(mockAuthTokenService.extractAccessToken(any())).thenReturn("expired_token");
        doThrow(new ExpiredAuthTokenException("expired token"))
                .when(mockAuthTokenService).parseAccessTokenAndGetMemberId("expired_token");
        when(mockAuthTokenService.extractRefreshToken(any())).thenReturn(null);

        AuthFilter authFilter = new AuthFilter(mockAuthTokenService, mock(AuthMemberService.class));
        FilterChain mockFilterChain = mock(FilterChain.class);

        authFilter.doFilterInternal(mock(HttpServletRequest.class), mock(HttpServletResponse.class), mockFilterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(mockFilterChain).doFilter(any(), any());
    }

    @Test
    void ExpiredAccessTokenAndExpiredRefreshTokenThenThrow() throws ServletException, IOException {
        AuthTokenService mockAuthTokenService = mock(AuthTokenService.class);
        when(mockAuthTokenService.extractAccessToken(any())).thenReturn("expired_token");
        doThrow(new ExpiredAuthTokenException("expired token"))
                .when(mockAuthTokenService).parseAccessTokenAndGetMemberId("expired_token");

        when(mockAuthTokenService.extractRefreshToken(any())).thenReturn("expired_refresh_token");
        doThrow(new ExpiredAuthTokenException("expired refresh token"))
                .when(mockAuthTokenService).parseRefreshTokenAndGetTokenId("expired_refresh_token");

        AuthFilter authFilter = new AuthFilter(mockAuthTokenService, mock(AuthMemberService.class));
        FilterChain mockFilterChain = mock(FilterChain.class);

        authFilter.doFilterInternal(mock(HttpServletRequest.class), mock(HttpServletResponse.class), mockFilterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(mockFilterChain).doFilter(any(), any());
    }

    @Test
    void NoAccessTokenButValidRefreshTokenThenAuthenticated() throws ServletException, IOException {
        AuthTokenService mockAuthTokenService = mock(AuthTokenService.class);
        AuthMemberService mockAuthMemberService = mock(AuthMemberService.class);

        // Setup: No access token, but valid refresh token
        when(mockAuthTokenService.extractAccessToken(any())).thenReturn(null);
        when(mockAuthTokenService.extractRefreshToken(any())).thenReturn("valid_refresh_token");
        when(mockAuthTokenService.parseRefreshTokenAndGetTokenId("valid_refresh_token")).thenReturn("token_id");

        UUID memberId = UUID.randomUUID();
        when(mockAuthTokenService.findMemberIdByRefreshTokenIdOrThrow("token_id")).thenReturn(memberId);

        // Setup: Token rotation
        when(mockAuthTokenService.createNewAccessToken(memberId)).thenReturn("new_access_token");
        when(mockAuthTokenService.createAndSaveRefreshToken(memberId)).thenReturn("new_refresh_token");

        // Setup: Member service
        AuthMember mockMember = mock(AuthMember.class);
        when(mockMember.role()).thenReturn(Role.REGULAR);
        when(mockAuthMemberService.findOrThrow(memberId)).thenReturn(mockMember);

        AuthFilter authFilter = new AuthFilter(mockAuthTokenService, mockAuthMemberService);
        FilterChain mockFilterChain = mock(FilterChain.class);

        authFilter.doFilterInternal(mock(HttpServletRequest.class), mock(HttpServletResponse.class), mockFilterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();

        // Verify token rotation occurred
        verify(mockAuthTokenService).deleteRefreshTokenById("token_id");
        verify(mockAuthTokenService).createNewAccessToken(memberId);
        verify(mockAuthTokenService).createAndSaveRefreshToken(memberId);
        verify(mockAuthTokenService).putAccessToken(any(), eq("new_access_token"));
        verify(mockAuthTokenService).putRefreshToken(any(), eq("new_refresh_token"));

        verify(mockFilterChain).doFilter(any(), any());
    }
}
