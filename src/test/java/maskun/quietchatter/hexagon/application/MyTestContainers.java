package maskun.quietchatter.hexagon.application;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
interface MyTestContainers {

    @Container
    PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("mydatabase")
            .withUsername("root")
            .withPassword("secret");

    @DynamicPropertySource
    static void containersProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresContainer::getUsername);
        registry.add("spring.datasource.password", postgresContainer::getPassword);
    }
}
