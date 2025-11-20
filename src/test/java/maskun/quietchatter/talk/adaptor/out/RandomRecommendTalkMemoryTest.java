package maskun.quietchatter.talk.adaptor.out;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

import maskun.quietchatter.talk.application.in.RecommendTalks;
import maskun.quietchatter.talk.domain.Talk;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest(properties = {"app.talk.recommend.interval-ms=60"})
@ActiveProfiles("test")
class RandomRecommendTalkMemoryTest {
    @MockitoBean
    private RandomTalkSampler talkSampler;

    @Autowired
    private RandomRecommendTalkMemory memory;

    @BeforeEach
    void setUp() {
        when(talkSampler.sample(anyInt()))
                .thenAnswer(ivc -> Instancio.ofList(Talk.class).size(ivc.getArgument(0)).create());

        memory.update();
    }

    @Test
    void get() throws InterruptedException {

        RecommendTalks talks = memory.get();

        assertThat(talks).isNotNull();
        assertThat(talks.items().size()).isEqualTo(RecommendTalks.MAX_SIZE);

        Thread.sleep(70);

        RecommendTalks cocurrentTalks = memory.get();
        assertThat(cocurrentTalks).isEqualTo(talks);

        RecommendTalks newTalks = memory.get();
        assertThat(newTalks).isNotEqualTo(talks);

    }
}
