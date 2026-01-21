package maskun.quietchatter.shared.persistence;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

@Configuration
@EnableRedisRepositories(basePackages = "none")
public class RedisConfig {
}
