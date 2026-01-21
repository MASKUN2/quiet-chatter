package maskun.quietchatter.security.internal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import java.sql.Date;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import javax.crypto.SecretKey;
import maskun.quietchatter.security.AuthTokenException;
import maskun.quietchatter.security.ExpiredAuthTokenException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

@ExtendWith(MockitoExtension.class)
class AuthTokenServiceTest {

    private static final String TEST_SECRET_KEY = "12345678901234567890123456789012";

    @Mock
    private RedisTemplate<String, UUID> redisTemplate;
    @Mock
    private ValueOperations<String, UUID> valueOperations;
    @Mock
    private HttpServletRequest request;

    private AuthTokenService authTokenService;

    @BeforeEach
    void setUp() {
    }

    private void initService() {
        authTokenService = new AuthTokenService(TEST_SECRET_KEY, redisTemplate);
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
    void createAndSaveRefreshToken_should_save_to_redis_and_return_token() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        initService();
        UUID memberId = UUID.randomUUID();

        String refreshToken = authTokenService.createAndSaveRefreshToken(memberId);

        assertThat(refreshToken).isNotNull();
        verify(valueOperations).set(any(String.class), eq(memberId), any(java.time.Duration.class));
        
        String tokenId = authTokenService.parseRefreshTokenAndGetTokenId(refreshToken);
        assertThat(tokenId).isNotEmpty();
    }

    @Test
    void findById_should_delegate_to_redis() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        initService();
        String tokenId = "some-token-id";
        UUID expectedMemberId = UUID.randomUUID();

        when(valueOperations.get("auth:refresh-token:" + tokenId)).thenReturn(expectedMemberId);

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
