package maskun.quietchatter.talk.adaptor.out;

import java.util.List;
import java.util.UUID;
import maskun.quietchatter.shared.persistence.JpaConfig;
import maskun.quietchatter.talk.domain.Talk;
import org.instancio.Instancio;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest(properties = "logging.level.org.springframework.jdbc=TRACE")
@Import({JpaConfig.class, RandomTalkSampler.class})
@ActiveProfiles("test")
class RandomTalkSamplerTest {
    @Autowired
    private RandomTalkSampler sampler;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {

        jdbcTemplate.update("Truncate talk");
        List<Object[]> args = Instancio.ofList(UUID.class).size(1000).create()
                .stream().map(uuid -> new Object[]{uuid})
                .toList();
        jdbcTemplate.batchUpdate("INSERT INTO talk(id) values (?)", args);

    }

    @AfterEach
    void tearDown() {
        jdbcTemplate.update("Truncate talk");
    }

    @Test
    void sample() {
        List<Talk> talks = sampler.sample(100);
        assert talks.size() == 100;

    }
}
