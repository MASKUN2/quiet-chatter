package maskun.quietchatter.security.adaptor;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import maskun.quietchatter.security.application.out.RefreshTokenCache;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;

import javax.crypto.SecretKey;
import java.sql.Date;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthTokenServiceTest {

    private static final String TEST_SECRET_KEY = "12345678901234567890123456789012";

    @Mock
    private RefreshTokenCache refreshTokenCache;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;

    private AuthTokenService authTokenService;

    @BeforeEach
    void setUp() {
    }

    private void initService() {
        authTokenService = new AuthTokenService(TEST_SECRET_KEY, new AppCookieProperties("localhost", false, "Lax"), refreshTokenCache);
    }

    @Test
    void putAccessToken_should_add_cookie_with_configured_properties() {
        // Given
        AppCookieProperties properties = new AppCookieProperties("example.com", true, "None");
        authTokenService = new AuthTokenService(TEST_SECRET_KEY, properties, refreshTokenCache);

        String accessToken = "test-token";

        // When
        authTokenService.putAccessToken(response, accessToken);

        // Then
        ArgumentCaptor<String> headerNameCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> headerValueCaptor = ArgumentCaptor.forClass(String.class);
        verify(response).addHeader(headerNameCaptor.capture(), headerValueCaptor.capture());

        assertThat(headerNameCaptor.getValue()).isEqualTo(HttpHeaders.SET_COOKIE);
        String cookieValue = headerValueCaptor.getValue();
        assertThat(cookieValue).contains("access_token=test-token");
        assertThat(cookieValue).contains("Domain=example.com");
        assertThat(cookieValue).contains("Secure");
        assertThat(cookieValue).contains("SameSite=None");
        assertThat(cookieValue).contains("HttpOnly");
        assertThat(cookieValue).contains("Path=/");
    }

    @Test
    void createNewAccessToken_should_generate_valid_token_with_correct_subject() {
        initService();
        UUID memberId = UUID.randomUUID();

        String token = authTokenService.createNewAccessToken(memberId);

        assertThat(token).isNotNull();
        UUID extractedId = authTokenService.parseAccessTokenAndGetMemberId(token);
        assertThat(extractedId).isEqualTo(memberId);
    }

    @Test
    void parseAccessTokenAndGetMemberId_should_throw_exception_when_expired() {
        initService();
        SecretKey key = Keys.hmacShaKeyFor(TEST_SECRET_KEY.getBytes());
        UUID memberId = UUID.randomUUID();

        String expiredToken = Jwts.builder()
                .signWith(key)
                .subject(memberId.toString())
                .expiration(Date.from(Instant.now().minus(1, ChronoUnit.HOURS)))
                .compact();

        assertThatThrownBy(() -> authTokenService.parseAccessTokenAndGetMemberId(expiredToken))
                .isInstanceOf(ExpiredAuthTokenException.class);
    }

    @Test
    void parseAccessTokenAndGetMemberId_should_throw_exception_when_malformed() {
        initService();
        String malformedToken = "invalid.token.value";

        assertThatThrownBy(() -> authTokenService.parseAccessTokenAndGetMemberId(malformedToken))
                .isInstanceOf(AuthTokenException.class);
    }

    @Test
    void createAndSaveRefreshToken_should_save_to_cache_and_return_token() {
        initService();
        UUID memberId = UUID.randomUUID();

        String refreshToken = authTokenService.createAndSaveRefreshToken(memberId);

        assertThat(refreshToken).isNotNull();
        verify(refreshTokenCache).save(any(String.class), eq(memberId), any(java.time.Duration.class));
        
        String tokenId = authTokenService.parseRefreshTokenAndGetTokenId(refreshToken);
        assertThat(tokenId).isNotEmpty();
    }

    @Test
    void findById_should_delegate_to_cache() {
        initService();
        String tokenId = "some-token-id";
        UUID expectedMemberId = UUID.randomUUID();

        when(refreshTokenCache.findMemberIdByRefreshTokenId(tokenId)).thenReturn(Optional.of(expectedMemberId));

        java.util.Optional<UUID> result = authTokenService.findById(tokenId);

        assertThat(result).isPresent().contains(expectedMemberId);
    }

    @Test
    void extractAccessToken_should_retrieve_from_cookie() {
        initService();
        Cookie cookie = new Cookie("access_token", "some-access-token");
        when(request.getCookies()).thenReturn(new Cookie[]{cookie});

        String token = authTokenService.extractAccessToken(request);

        assertThat(token).isEqualTo("some-access-token");
    }

    @Test
    void extractAccessToken_should_retrieve_from_header_when_cookie_missing() {
        initService();
        when(request.getCookies()).thenReturn(null);
        when(request.getHeader("Authorization")).thenReturn("Bearer header-access-token");

        String token = authTokenService.extractAccessToken(request);

        assertThat(token).isEqualTo("header-access-token");
    }

    @Test
    void extractAccessToken_should_prioritize_cookie_over_header() {
        initService();
        Cookie cookie = new Cookie("access_token", "cookie-access-token");
        when(request.getCookies()).thenReturn(new Cookie[]{cookie});
        // Header should be ignored if cookie is present
        // Note: In a real scenario, we might not set up the mock for header if it's not called,
        // but strict stubs might complain. However, the logic stops at cookie.

        String token = authTokenService.extractAccessToken(request);

        assertThat(token).isEqualTo("cookie-access-token");
    }

    @Test
    void extractRefreshToken_should_retrieve_from_cookie() {
        initService();
        Cookie cookie = new Cookie("refresh_token", "some-refresh-token");
        when(request.getCookies()).thenReturn(new Cookie[]{cookie});

        String token = authTokenService.extractRefreshToken(request);

        assertThat(token).isEqualTo("some-refresh-token");
    }

    @Test
    void extractAccessToken_should_return_null_if_cookie_missing() {
        initService();
        when(request.getCookies()).thenReturn(new Cookie[]{});

        String token = authTokenService.extractAccessToken(request);

        assertThat(token).isNull();
    }
}
