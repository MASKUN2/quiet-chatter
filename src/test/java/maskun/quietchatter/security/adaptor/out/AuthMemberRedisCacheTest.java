package maskun.quietchatter.security.adaptor.out;

import maskun.quietchatter.WithTestContainerDatabases;
import maskun.quietchatter.member.domain.Role;
import maskun.quietchatter.security.domain.AuthMember;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
class AuthMemberRedisCacheTest implements WithTestContainerDatabases {

    @Autowired
    private AuthMemberRedisCache authMemberRedisCache;

    @Autowired
    private RedisTemplate<String, AuthMember> redisTemplate;

    @BeforeEach
    void cleanupRedis() {
        redisTemplate.execute((RedisCallback<Object>) conn -> {
            conn.serverCommands().flushDb();
            return null;
        });
    }

    @Test
    void serializableValue() {
        AuthMember authMember = new AuthMember(UUID.randomUUID(), Role.GUEST);
        RedisSerializer<AuthMember> valueSerializer = (RedisSerializer<AuthMember>) redisTemplate.getValueSerializer();
        byte[] serialized = valueSerializer.serialize(authMember);
        assertThat(serialized).isNotNull();
        AuthMember deserialized = valueSerializer.deserialize(serialized);
        assertThat(deserialized).isEqualTo(authMember);
    }

    @Test
    void saveAndFind() {
        UUID id = UUID.randomUUID();
        AuthMember authMember = new AuthMember(id, Role.GUEST);

        authMemberRedisCache.save(authMember);
        AuthMember found = authMemberRedisCache.findById(id).orElseThrow();

        assertThat(found).isEqualTo(authMember);
    }
}
