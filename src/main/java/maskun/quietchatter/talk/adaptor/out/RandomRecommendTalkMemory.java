package maskun.quietchatter.talk.adaptor.out;

import jakarta.annotation.PostConstruct;
import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import lombok.extern.slf4j.Slf4j;
import maskun.quietchatter.talk.application.in.RecommendTalks;
import maskun.quietchatter.talk.application.out.RecommendTalkRepository;
import maskun.quietchatter.talk.domain.Talk;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

@Slf4j
@Component
class RandomRecommendTalkMemory implements RecommendTalkRepository {
    private final Duration intervalTime;
    private final RandomTalkSampler talkSampler;
    private final ThreadPoolTaskExecutor cacheUpdateExecutor;

    private final AtomicReference<Set<Talk>> cachedTalks = new AtomicReference<>(Collections.emptySet());
    private final AtomicReference<Instant> lastUpdatedAt = new AtomicReference<>(Instant.now());

    RandomRecommendTalkMemory(@Value("${app.talk.recommend.interval-ms:5000}") long ms,
                              RandomTalkSampler talkSampler,
                              @Qualifier("cacheUpdateExecutor") ThreadPoolTaskExecutor cacheUpdateExecutor) {
        this.intervalTime = Duration.ofMillis(ms);
        this.talkSampler = talkSampler;

        this.cacheUpdateExecutor = cacheUpdateExecutor;
    }

    @Override
    public RecommendTalks get() {
        List<Talk> cached = List.copyOf(this.cachedTalks.get());
        updateIfNeed();
        return new RecommendTalks(cached);
    }

    private void updateIfNeed() {
        Instant now = Instant.now();

        Instant lastUpdated = lastUpdatedAt.get();

        if (Duration.between(lastUpdated, now).compareTo(intervalTime) > 0) {
            if (lastUpdatedAt.compareAndSet(lastUpdated, now)) {
                cacheUpdateExecutor.submit(this::update);
            }
        }
    }

    @PostConstruct
    void init() {
        update();
    }

    public void update() {
        log.debug("update 실행스레드:{}", Thread.currentThread().getName());
        try {
            List<Talk> sample = talkSampler.sample(RecommendTalks.MAX_SIZE);
            cachedTalks.set(Set.copyOf(sample));
            lastUpdatedAt.set(Instant.now());
        } catch (Exception e) {
            log.error("메모리 캐시 업데이트 중 오류 발생", e);
        }

    }

}
