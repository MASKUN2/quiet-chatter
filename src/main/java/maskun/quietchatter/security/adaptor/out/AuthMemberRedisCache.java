package maskun.quietchatter.security.adaptor.out;

import lombok.RequiredArgsConstructor;
import maskun.quietchatter.security.application.out.AuthMemberCache;
import maskun.quietchatter.security.domain.AuthMember;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
class AuthMemberRedisCache implements AuthMemberCache {
    private static final String KEY_PREFIX = "auth:member:";
    private final RedisTemplate<String, AuthMember> redisTemplate;

    @Override
    public Optional<AuthMember> findById(UUID id) {
        return Optional.ofNullable(redisTemplate.opsForValue().get(KEY_PREFIX + id));
    }

    @Override
    public void save(AuthMember authMember) {
        redisTemplate.opsForValue().set(
                KEY_PREFIX + authMember.id(),
                authMember,
                Duration.ofHours(2)
        );
    }
}
