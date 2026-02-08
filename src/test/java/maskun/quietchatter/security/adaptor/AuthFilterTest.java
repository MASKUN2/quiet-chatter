package maskun.quietchatter.security.adaptor;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import maskun.quietchatter.security.application.in.AuthMemberNotFoundException;
import maskun.quietchatter.security.application.in.AuthMemberService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.*;

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
    void ExpiredAccessTokenAndExpiredRefreshTokenThenNotAuthenticated() throws ServletException, IOException {
        AuthTokenService mockAuthTokenService = mock(AuthTokenService.class);
        when(mockAuthTokenService.extractAccessToken(any())).thenReturn("some expired token");
        when(mockAuthTokenService.parseAccessTokenAndGetMemberId(any())).thenThrow(
                new ExpiredAuthTokenException("some expired token"));
        when(mockAuthTokenService.extractRefreshToken(any())).thenReturn("some expired refresh token");
        when(mockAuthTokenService.parseRefreshTokenAndGetTokenId(any())).thenThrow(
                new ExpiredAuthTokenException("some expired refresh token"));

        AuthFilter authFilter = new AuthFilter(mockAuthTokenService, mock(AuthMemberService.class));

        FilterChain mockfilterChain = mock(FilterChain.class);

        assertThatCode(() -> authFilter.doFilterInternal(
                mock(HttpServletRequest.class), mock(HttpServletResponse.class), mockfilterChain)
        ).doesNotThrowAnyException();

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(mockfilterChain).doFilter(any(), any());
    }

    @Test
    void NoAccessTokenButRefreshTokenNotFoundInRedis() throws ServletException, IOException {
        AuthTokenService mockAuthTokenService = mock(AuthTokenService.class);
        when(mockAuthTokenService.extractAccessToken(any())).thenReturn(null);
        when(mockAuthTokenService.extractRefreshToken(any())).thenReturn("refresh_token_not_in_redis");
        when(mockAuthTokenService.parseRefreshTokenAndGetTokenId(any())).thenReturn("token_id");
        when(mockAuthTokenService.findMemberIdByRefreshTokenIdOrThrow(any())).thenThrow(new NoSuchElementException("refresh token not found"));

        AuthFilter authFilter = new AuthFilter(mockAuthTokenService, mock(AuthMemberService.class));
        FilterChain mockfilterChain = mock(FilterChain.class);

        assertThatCode(() -> authFilter.doFilterInternal(
                mock(HttpServletRequest.class), mock(HttpServletResponse.class), mockfilterChain)
        ).doesNotThrowAnyException();

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(mockfilterChain).doFilter(any(), any());
    }

    @Test
    void NoAccessTokenButMemberNotFound() throws ServletException, IOException {
        AuthTokenService mockAuthTokenService = mock(AuthTokenService.class);
        AuthMemberService mockAuthMemberService = mock(AuthMemberService.class);

        when(mockAuthTokenService.extractAccessToken(any())).thenReturn(null);
        when(mockAuthTokenService.extractRefreshToken(any())).thenReturn("valid_refresh_token");
        when(mockAuthTokenService.parseRefreshTokenAndGetTokenId(any())).thenReturn("token_id");
        when(mockAuthTokenService.findMemberIdByRefreshTokenIdOrThrow(any())).thenReturn(java.util.UUID.randomUUID());

        when(mockAuthMemberService.findOrThrow(any())).thenThrow(new AuthMemberNotFoundException("member not found"));

        AuthFilter authFilter = new AuthFilter(mockAuthTokenService, mockAuthMemberService);
        FilterChain mockfilterChain = mock(FilterChain.class);

        assertThatCode(() -> authFilter.doFilterInternal(
                mock(HttpServletRequest.class), mock(HttpServletResponse.class), mockfilterChain)
        ).doesNotThrowAnyException();

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(mockfilterChain).doFilter(any(), any());
    }

    @Test
    void NoAccessTokenButInvalidRefreshToken() throws ServletException, IOException {
        AuthTokenService mockAuthTokenService = mock(AuthTokenService.class);
        when(mockAuthTokenService.extractAccessToken(any())).thenReturn(null);
        when(mockAuthTokenService.extractRefreshToken(any())).thenReturn("invalid_refresh_token");
        when(mockAuthTokenService.parseRefreshTokenAndGetTokenId(any())).thenThrow(new AuthTokenException("invalid token"));

        AuthFilter authFilter = new AuthFilter(mockAuthTokenService, mock(AuthMemberService.class));
        FilterChain mockfilterChain = mock(FilterChain.class);

        assertThatCode(() -> authFilter.doFilterInternal(
                mock(HttpServletRequest.class), mock(HttpServletResponse.class), mockfilterChain)
        ).doesNotThrowAnyException();

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(mockfilterChain).doFilter(any(), any());
    }

    @Test
    void NoAccessTokenButValidRefreshTokenThenAuthenticated() throws ServletException, IOException {
        AuthTokenService mockAuthTokenService = mock(AuthTokenService.class);
        AuthMemberService mockAuthMemberService = mock(AuthMemberService.class);

        when(mockAuthTokenService.extractAccessToken(any())).thenReturn(null);
        when(mockAuthTokenService.extractRefreshToken(any())).thenReturn("valid_refresh_token");
        when(mockAuthTokenService.parseRefreshTokenAndGetTokenId(any())).thenReturn("token_id");
        when(mockAuthTokenService.findMemberIdByRefreshTokenIdOrThrow(any())).thenReturn(java.util.UUID.randomUUID());
        when(mockAuthTokenService.createNewAccessToken(any())).thenReturn("new_access_token");
        when(mockAuthTokenService.createAndSaveRefreshToken(any())).thenReturn("new_refresh_token");

        maskun.quietchatter.security.domain.AuthMember mockMember = mock(maskun.quietchatter.security.domain.AuthMember.class);
        when(mockMember.role()).thenReturn(maskun.quietchatter.member.domain.Role.REGULAR);
        when(mockAuthMemberService.findOrThrow(any())).thenReturn(mockMember);

        AuthFilter authFilter = new AuthFilter(mockAuthTokenService, mockAuthMemberService);
        FilterChain mockfilterChain = mock(FilterChain.class);

        authFilter.doFilterInternal(mock(HttpServletRequest.class), mock(HttpServletResponse.class), mockfilterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
        verify(mockAuthTokenService).createNewAccessToken(any());
        verify(mockAuthTokenService).createAndSaveRefreshToken(any());
        verify(mockfilterChain).doFilter(any(), any());
    }
}
