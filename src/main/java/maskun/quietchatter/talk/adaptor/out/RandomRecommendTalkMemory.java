package maskun.quietchatter.talk.adaptor.out;

import jakarta.annotation.PostConstruct;
import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import maskun.quietchatter.talk.application.in.RecommendTalks;
import maskun.quietchatter.talk.application.out.RecommendTalkRepository;
import maskun.quietchatter.talk.domain.Talk;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
class RandomRecommendTalkMemory implements RecommendTalkRepository {
    private final Duration intervalTime;
    private final RandomTalkSampler talkSampler;
    private final ApplicationEventPublisher eventPublisher;

    private final AtomicReference<Set<Talk>> cachedTalks = new AtomicReference<>(Collections.emptySet());
    private final AtomicReference<Instant> lastUpdatedAt = new AtomicReference<>(Instant.now());

    RandomRecommendTalkMemory(@Value("${app.talk.recommend.interval-ms:5000}") long ms,
                              RandomTalkSampler talkSampler,
                              ApplicationEventPublisher eventPublisher) {
        this.intervalTime = Duration.ofMillis(ms);
        this.talkSampler = talkSampler;
        this.eventPublisher = eventPublisher;
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
                eventPublisher.publishEvent(new CacheUpdateEvent());
            }
        }
    }

    @PostConstruct
    void init() {
        update();
    }

    @Async
    @EventListener(CacheUpdateEvent.class)
    public void update() {
        List<Talk> sample = talkSampler.sample(RecommendTalks.MAX_SIZE);
        cachedTalks.set(Set.copyOf(sample));
        lastUpdatedAt.set(Instant.now());
    }

    record CacheUpdateEvent() {
    }

}
