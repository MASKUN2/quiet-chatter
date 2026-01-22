package maskun.quietchatter.security.internal;

import java.util.UUID;
import maskun.quietchatter.security.AuthMember;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
class RedisTemplateConfig {
    @Bean
    RedisTemplate<String, AuthMember> authMemberRedisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, AuthMember> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new Jackson2JsonRedisSerializer<>(AuthMember.class));
        return template;
    }

    @Bean
    RedisTemplate<String, UUID> refreshTokenRedisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, UUID> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new Jackson2JsonRedisSerializer<>(UUID.class));
        return template;
    }
}
