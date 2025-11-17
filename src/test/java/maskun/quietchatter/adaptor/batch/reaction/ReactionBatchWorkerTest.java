package maskun.quietchatter.adaptor.batch.reaction;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import maskun.quietchatter.adaptor.jpa.JpaConfig;
import org.instancio.Instancio;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest(properties = "logging.level.org.springframework.jdbc=TRACE")
@Import({JpaConfig.class, ReactionBatchWorker.class})
@ActiveProfiles("test")
class ReactionBatchWorkerTest {
    @Autowired
    private ReactionBatchWorker worker;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        Long count = getCount();
        assertThat(count).isEqualTo(0L);

    }

    private @Nullable Long getCount() {
        return jdbcTemplate.queryForObject("SELECT count(reaction.id) FROM reaction", Long.class);
    }

    @Test
    @DisplayName("정상적으로 배치작업이 이뤄지는지 테스트")
    void insertBatch() {
        List<ReactionTarget> targets = Instancio.ofList(ReactionTarget.class).size(10).create();

        worker.insertBatch(targets);
        worker.insertBatch(targets); //duplicated insert 아무것도안함
        assertThat(getCount()).isEqualTo(10L);
        assertThat(getInvalidCount()).isEqualTo(0L);

        worker.deleteBatch(targets);
        assertThat(getCount()).isEqualTo(0L);
    }

    private @Nullable Long getInvalidCount() {
        return jdbcTemplate.queryForObject(
                "SELECT count(reaction.id) FROM reaction "
                        + "WHERE created_at IS NULL or member_id IS NULL or talk_id IS NULL",
                Long.class);
    }
}

