package maskun.quietchatter.shared.persistence;

import maskun.quietchatter.shared.security.AuthMember;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@EnableRedisRepositories(basePackages = "none")
public class RedisConfig {
    @Bean
    RedisTemplate<String, AuthMember> authMemberRedisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, AuthMember> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new Jackson2JsonRedisSerializer<>(AuthMember.class));
        return template;
    }
}
