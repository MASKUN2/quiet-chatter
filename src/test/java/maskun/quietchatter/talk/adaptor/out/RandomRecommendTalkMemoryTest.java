package maskun.quietchatter.talk.adaptor.out;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

import maskun.quietchatter.talk.application.in.RecommendTalks;
import maskun.quietchatter.talk.domain.Talk;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@ExtendWith(MockitoExtension.class)
class RandomRecommendTalkMemoryTest {
    @Mock
    private RandomTalkSampler talkSampler;

    @Mock
    private ThreadPoolTaskExecutor cacheUpdateExecutor;

    private RandomRecommendTalkMemory memory;

    @BeforeEach
    void setUp() {
        when(talkSampler.sample(anyInt()))
                .thenAnswer(ivc -> Instancio.ofList(Talk.class).size(ivc.getArgument(0)).create());

        doAnswer(invocation -> {
            Runnable command = invocation.getArgument(0);
            command.run();
            return null;
        }).when(cacheUpdateExecutor).submit(Mockito.any(Runnable.class));

        memory = new RandomRecommendTalkMemory(
                20L,
                talkSampler,
                cacheUpdateExecutor
        );
        memory.init();
    }

    @Test
    void get() throws InterruptedException {
        RecommendTalks talks = memory.get();

        assertThat(talks).isNotNull();
        assertThat(talks.items().size()).isEqualTo(RecommendTalks.MAX_SIZE);
        Thread.sleep(20);
        RecommendTalks cocurrentTalks = memory.get();
        assertThat(cocurrentTalks).isEqualTo(talks);

        RecommendTalks newTalks = memory.get();
        assertThat(newTalks).isNotEqualTo(talks);

    }
}
