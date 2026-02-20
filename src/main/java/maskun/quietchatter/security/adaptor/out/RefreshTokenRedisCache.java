package maskun.quietchatter.security.adaptor.out;

import lombok.RequiredArgsConstructor;
import maskun.quietchatter.security.application.out.RefreshTokenCache;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
class RefreshTokenRedisCache implements RefreshTokenCache {
    private static final String REDIS_REFRESH_TOKEN_PREFIX = "auth:refresh-token:";
    private final RedisTemplate<String, UUID> redisTemplate;

    @Override
    public void save(String refreshTokenId, UUID memberId, Duration duration) {
        redisTemplate.opsForValue().set(REDIS_REFRESH_TOKEN_PREFIX + refreshTokenId, memberId, duration);
    }

    @Override
    public Optional<UUID> findMemberIdByRefreshTokenId(String refreshTokenId) {
        return Optional.ofNullable(redisTemplate.opsForValue().get(REDIS_REFRESH_TOKEN_PREFIX + refreshTokenId));
    }

    @Override
    public void deleteByRefreshTokenId(String refreshTokenId) {
        redisTemplate.delete(REDIS_REFRESH_TOKEN_PREFIX + refreshTokenId);
    }
}
