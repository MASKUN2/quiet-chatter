package maskun.quietchatter.security.internal;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.sql.Date;
import java.time.Duration;
import java.time.Instant;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import javax.crypto.SecretKey;
import maskun.quietchatter.security.AuthTokenException;
import maskun.quietchatter.security.ExpiredAuthTokenException;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@NullMarked
@Service
class AuthTokenService {
    private static final String ACCESS_TOKEN_COOKIE_NAME = "access_token";
    private static final String REFRESH_TOKEN_COOKIE_NAME = "refresh_token";
    private static final Duration ACCESS_TOKEN_LIFE_TIME = Duration.ofMinutes(30);
    private static final Duration REFRESH_TOKEN_LIFETIME = Duration.ofDays(30);
    private static final String REDIS_REFRESH_TOKEN_PREFIX = "auth:refresh-token:";

    private final RedisTemplate<String, UUID> redisTemplate;
    private final JwtParser jwtParser;
    private final SecretKey secretKey;

    AuthTokenService(@Value("${jwt.secret-key}") String rawKey, RedisTemplate<String, UUID> redisTemplate) {
        this.secretKey = Keys.hmacShaKeyFor(rawKey.getBytes());
        this.redisTemplate = redisTemplate;
        this.jwtParser = Jwts.parser().verifyWith(this.secretKey).build();
    }

    public String parseRefreshTokenAndGetTokenId(String refreshToken)throws AuthTokenException{
        Claims payload = parse(refreshToken).getPayload();
        return payload.getId();
    }

    public @Nullable String extractAccessToken(HttpServletRequest request) {
        return findCookieValueOrNull(request, ACCESS_TOKEN_COOKIE_NAME);
    }

    public @Nullable String extractRefreshToken(HttpServletRequest request) {
        return findCookieValueOrNull(request, REFRESH_TOKEN_COOKIE_NAME);
    }

    public String createNewAccessToken(UUID memberId) {
        var exp = Date.from(Instant.now().plus(ACCESS_TOKEN_LIFE_TIME));
        return Jwts.builder().signWith(secretKey)
                .subject(memberId.toString())
                .expiration(exp)
                .compact();
    }

    public void putAccessToken(HttpServletResponse response, String accessToken) {
        Cookie cookie = new Cookie(ACCESS_TOKEN_COOKIE_NAME, accessToken);
        cookie.setMaxAge((int) ACCESS_TOKEN_LIFE_TIME.getSeconds());
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        response.addCookie(cookie);

    }

    public void putRefreshToken(HttpServletResponse response, String newRefreshToken) {
        Cookie cookie = new Cookie(REFRESH_TOKEN_COOKIE_NAME, newRefreshToken);
        cookie.setMaxAge((int) REFRESH_TOKEN_LIFETIME.getSeconds());
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    public Optional<UUID> findById(String id) {
        return Optional.ofNullable(redisTemplate.opsForValue().get(REDIS_REFRESH_TOKEN_PREFIX + id));
    }

    public UUID findMemberIdByRefreshTokenIdOrThrow(String id) throws NoSuchElementException {
        return findById(id).orElseThrow(() -> new NoSuchElementException("refresh token not found for id: " + id));
    }

    public void deleteRefreshTokenById(String id) {
        redisTemplate.delete(REDIS_REFRESH_TOKEN_PREFIX + id);
    }

    public String createAndSaveRefreshToken(UUID memberId) {
        String refreshTokenId = UUID.randomUUID().toString();
        redisTemplate.opsForValue().set(REDIS_REFRESH_TOKEN_PREFIX + refreshTokenId, memberId, REFRESH_TOKEN_LIFETIME);
        var exp = Date.from(Instant.now().plus(REFRESH_TOKEN_LIFETIME));
        return Jwts.builder().signWith(secretKey)
                .id(refreshTokenId)
                .expiration(exp)
                .compact();
    }

    UUID parseAccessTokenAndGetMemberId(String accessToken) {
        Claims payload = parse(accessToken).getPayload();
        return UUID.fromString(payload.getSubject());
    }

    private @Nullable String findCookieValueOrNull(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }

        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(name)) {
                return cookie.getValue();
            }
        }
        return null;
    }

    private Jws<Claims> parse(String authToken) {
        try {
            return jwtParser.parseSignedClaims(authToken);
        } catch (ExpiredJwtException e) {
            throw new ExpiredAuthTokenException("expired token", e);
        } catch (JwtException | IllegalArgumentException e) {
            throw new AuthTokenException("invalid token", e);
        }
    }
}
