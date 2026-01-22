package maskun.quietchatter;

import com.redis.testcontainers.RedisContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.utility.DockerImageName;

public interface WithTestContainerDatabases {

    @ServiceConnection
    RedisContainer REDIS = createRedis();

    @ServiceConnection
    PostgreSQLContainer<?> POSTGRESQL = createPostgres();

    private static RedisContainer createRedis() {
        RedisContainer container = new RedisContainer(DockerImageName.parse("redis:8.2-alpine"));
        container.start();
        container.withReuse(true);
        return container;
    }

    private static PostgreSQLContainer<?> createPostgres() {
        PostgreSQLContainer<?> container = new PostgreSQLContainer<>(DockerImageName.parse("postgres:16-alpine"));
        container.start();
        container.withReuse(true);
        return container;
    }
}