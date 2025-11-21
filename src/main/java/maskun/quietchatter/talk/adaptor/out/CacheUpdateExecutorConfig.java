package maskun.quietchatter.talk.adaptor.out;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
class CacheUpdateExecutorConfig {
    @Bean
    ThreadPoolTaskExecutor cacheUpdateExecutor() {

        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(1);
        executor.setMaxPoolSize(1);
        executor.setThreadNamePrefix("Cache-Updater");
        executor.initialize();

        return executor;
    }
}
