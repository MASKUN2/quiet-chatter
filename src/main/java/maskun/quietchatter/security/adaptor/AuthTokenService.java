package maskun.quietchatter.security.adaptor;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.sql.Date;
import java.time.Duration;
import java.time.Instant;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

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
    private final AppCookieProperties appCookieProperties;

    AuthTokenService(@Value("${jwt.secret-key}") String rawKey,
                     AppCookieProperties appCookieProperties,
                     RedisTemplate<String, UUID> redisTemplate) {
        this.secretKey = Keys.hmacShaKeyFor(rawKey.getBytes());
        this.redisTemplate = redisTemplate;
        this.jwtParser = Jwts.parser().verifyWith(this.secretKey).build();
        this.appCookieProperties = appCookieProperties;
    }

    public String parseRefreshTokenAndGetTokenId(String refreshToken) throws AuthTokenException {
        Claims payload = parse(refreshToken).getPayload();
        return payload.getId();
    }

    public @Nullable String extractAccessToken(HttpServletRequest request) {
        String cookieValue = findCookieValueOrNull(request, ACCESS_TOKEN_COOKIE_NAME);
        if (cookieValue != null) {
            return cookieValue;
        }

        return extractBearerToken(request);
    }

    private @Nullable String extractBearerToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
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
        addCookie(response, ACCESS_TOKEN_COOKIE_NAME, accessToken, ACCESS_TOKEN_LIFE_TIME);
    }

    public void putRefreshToken(HttpServletResponse response, String newRefreshToken) {
        addCookie(response, REFRESH_TOKEN_COOKIE_NAME, newRefreshToken, REFRESH_TOKEN_LIFETIME);
    }

    private void addCookie(HttpServletResponse response, String name, String value, Duration maxAge) {
        org.springframework.http.ResponseCookie.ResponseCookieBuilder builder = org.springframework.http.ResponseCookie.from(name, value)
                .path("/")
                .httpOnly(true)
                .maxAge(maxAge)
                .secure(appCookieProperties.secure())
                .sameSite(appCookieProperties.sameSite())
                .domain(appCookieProperties.domain());

        response.addHeader(org.springframework.http.HttpHeaders.SET_COOKIE, builder.build().toString());
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
