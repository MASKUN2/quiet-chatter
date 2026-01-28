package maskun.quietchatter.talk.adaptor.out;

import static org.instancio.Instancio.ofList;

import jakarta.persistence.EntityManager;
import java.util.List;
import maskun.quietchatter.WithTestContainerDatabases;
import maskun.quietchatter.shared.persistence.BaseEntity;
import maskun.quietchatter.talk.application.out.TalkRepository;
import maskun.quietchatter.talk.domain.Talk;
import org.instancio.Select;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

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
}
