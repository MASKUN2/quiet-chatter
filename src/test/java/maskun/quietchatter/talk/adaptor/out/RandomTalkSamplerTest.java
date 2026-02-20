package maskun.quietchatter.talk.adaptor.out;

import jakarta.persistence.EntityManager;
import maskun.quietchatter.WithTestContainerDatabases;
import maskun.quietchatter.persistence.BaseEntity;
import maskun.quietchatter.talk.application.out.TalkRepository;
import maskun.quietchatter.talk.domain.Talk;
import org.instancio.Select;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.instancio.Instancio.ofList;

@SpringBootTest(properties = "logging.level.org.springframework.jdbc=TRACE")
@Transactional
class RandomTalkSamplerTest implements WithTestContainerDatabases {
    @Autowired
    private RandomTalkSampler sampler;

    @Autowired
    private TalkRepository talkRepository;

    @Autowired
    private EntityManager entityManager;

    @BeforeEach
    void setUp() {
        List<Talk> talks = ofList(Talk.class).size(1000)
                .ignore(Select.all(Select.fields().declaredIn(BaseEntity.class)))
                .create();
        talkRepository.saveAll(talks);
        entityManager.flush();
        entityManager.clear();

    }

    @AfterEach
    void tearDown() {
        entityManager.createQuery("DELETE FROM talk").executeUpdate();
    }

    @Test
    void sample() {
        List<Talk> talks = sampler.sample(100);
        assert talks.size() == 100;

    }

    @Test
    void sampleExcludesHiddenTalks() {
        // Given: some talks are hidden
        List<Talk> allTalks = talkRepository.findAllByIdIn(List.of()); // dummy to get all if needed, but we use saveAll
        for (int i = 0; i < 50; i++) {
            Talk talk = new Talk(java.util.UUID.randomUUID(), java.util.UUID.randomUUID(), "nick", "content");
            talk.hide();
            talkRepository.save(talk);
        }
        entityManager.flush();
        entityManager.clear();

        // When
        List<Talk> sampled = sampler.sample(1000);

        // Then
        boolean containsHidden = sampled.stream().anyMatch(Talk::isHidden);
        assert !containsHidden : "Sampled talks should not contain hidden talks";
    }
}
