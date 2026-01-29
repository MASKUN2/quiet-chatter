package maskun.quietchatter;

import com.redis.testcontainers.RedisContainer;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.Container;
import org.testcontainers.containers.PostgreSQLContainer;
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

    static void clearAll() {
        // PostgreSQL 클린업
        try {
            String truncateAll = "TRUNCATE TABLE " +
                    String.join(", ", getTableNames()) + " CASCADE";
            POSTGRESQL.execInContainer("psql", "-U", POSTGRESQL.getUsername(), "-c", truncateAll);
        } catch (Exception ignored) {}

        // Redis 클린업
        try {
            REDIS.execInContainer("redis-cli", "FLUSHALL");
        } catch (Exception ignored) {}
    }

    private static List<String> getTableNames() {
        try {
            // -t: 헤더/푸터 제거, -A: 정렬되지 않은 출력, -c: 쿼리 실행
            String query = "SELECT tablename FROM pg_tables WHERE schemaname = 'public'";
            Container.ExecResult result = POSTGRESQL.execInContainer(
                    "psql", "-U", POSTGRESQL.getUsername(), "-d", POSTGRESQL.getDatabaseName(),
                    "-t", "-A", "-c", query
            );

            if (result.getExitCode() != 0) {
                throw new RuntimeException("Failed to fetch table names: " + result.getStderr());
            }

            return Arrays.stream(result.getStdout().split("\n"))
                    .map(String::trim)
                    .filter(name -> !name.isEmpty())
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error during fetching tables from container", e);
        }
    }
}