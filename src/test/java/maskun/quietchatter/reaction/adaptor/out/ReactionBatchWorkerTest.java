package maskun.quietchatter.reaction.adaptor.out;

import maskun.quietchatter.WithTestContainerDatabases;
import maskun.quietchatter.reaction.domain.Reaction;
import maskun.quietchatter.shared.persistence.JpaConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("SqlWithoutWhere")
@DataJpaTest(properties = "logging.level.org.springframework.jdbc=TRACE")
@Import({JpaConfig.class, ReactionBatchWorker.class})
class ReactionBatchWorkerTest implements WithTestContainerDatabases {

    @Autowired
    private ReactionBatchWorker worker;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        jdbcTemplate.update("DELETE FROM reaction");
        jdbcTemplate.update("DELETE FROM talk");
        assertThat(getReactionCount()).isEqualTo(0L);
    }

    @Test
    @DisplayName("INSERT와 DELETE를 혼합하여 처리하고, 중복 이벤트가 무시되는지 테스트")
    void process_with_mixed_and_duplicated_events() {
        UUID talkId1 = UUID.randomUUID();
        UUID talkId2 = UUID.randomUUID();
        UUID talkId3 = UUID.randomUUID();

        UUID memberId1 = UUID.randomUUID();
        UUID memberId2 = UUID.randomUUID();
        UUID memberId3 = UUID.randomUUID();

        jdbcTemplate.update(
                "INSERT INTO talk (id, member_id, content, like_count, support_count, created_at, last_modified_at) VALUES (?, ?, ?, 0, 0, now(), now())",
                talkId1, UUID.randomUUID(), "Talk 1");
        jdbcTemplate.update(
                "INSERT INTO talk (id, member_id, content, like_count, support_count, created_at, last_modified_at) VALUES (?, ?, ?, 0, 0, now(), now())",
                talkId2, UUID.randomUUID(), "Talk 2");
        jdbcTemplate.update(
                "INSERT INTO talk (id, member_id, content, like_count, support_count, created_at, last_modified_at) VALUES (?, ?, ?, 0, 0, now(), now())",
                talkId3, UUID.randomUUID(), "Talk 3");

        // 2. insert와 delete를 혼합해서. 일부 겹치게 해서 넣어줘
        List<ReactionEvent> events = List.of(
                // Talk 1 시나리오: like_count=2, support_count=1 이 되어야 함.
                new ReactionEvent(talkId1, memberId1, Reaction.Type.LIKE, Action.INSERT),
                new ReactionEvent(talkId1, memberId2, Reaction.Type.LIKE, Action.INSERT),
                new ReactionEvent(talkId1, memberId3, Reaction.Type.SUPPORT, Action.INSERT),
                new ReactionEvent(talkId1, memberId3, Reaction.Type.SUPPORT, Action.INSERT), // 4. 겹치는 INSERT 이벤트

                // Talk 2 시나리오: support_count=2 가 되어야 함.
                new ReactionEvent(talkId2, memberId1, Reaction.Type.SUPPORT, Action.INSERT),
                new ReactionEvent(talkId2, memberId2, Reaction.Type.SUPPORT, Action.INSERT),

                // Talk 3 시나리오: INSERT/DELETE가 서로 상쇄되어 count=0 이 되어야 함.
                new ReactionEvent(talkId3, memberId1, Reaction.Type.LIKE, Action.INSERT),
                new ReactionEvent(talkId3, memberId1, Reaction.Type.LIKE, Action.DELETE),

                // Talk 3 시나리오: 중복 DELETE 테스트
                new ReactionEvent(talkId3, memberId2, Reaction.Type.LIKE, Action.INSERT), // reaction 레코드 생성
                new ReactionEvent(talkId3, memberId2, Reaction.Type.LIKE, Action.DELETE), // reaction 레코드 삭제
                new ReactionEvent(talkId3, memberId2, Reaction.Type.LIKE, Action.DELETE)  // 4. 겹치는 DELETE 이벤트
        );

        // when
        ReactionRequestAggregator aggregator = new ReactionRequestAggregator(events);
        worker.process(aggregator);

        // then
        // 최종 reaction 레코드 개수 검증
        // talk_id=1 (3개), talk_id=2 (2개), talk_id=3 (0개) -> 총 5개
        assertThat(getReactionCount()).isEqualTo(5L);

        // 3. talk 별 like/support 카운트 검증
        Map<String, Object> talk1Counts = getTalkReactionCounts(talkId1);
        assertThat(talk1Counts.get("like_count")).isEqualTo(2L);
        assertThat(talk1Counts.get("support_count")).isEqualTo(1L);

        Map<String, Object> talk2Counts = getTalkReactionCounts(talkId2);
        assertThat(talk2Counts.get("like_count")).isEqualTo(0L);
        assertThat(talk2Counts.get("support_count")).isEqualTo(2L);

        Map<String, Object> talk3Counts = getTalkReactionCounts(talkId3);
        assertThat(talk3Counts.get("like_count")).isEqualTo(0L);
        assertThat(talk3Counts.get("support_count")).isEqualTo(0L);
    }

    private Long getReactionCount() {
        return jdbcTemplate.queryForObject("SELECT count(*) FROM reaction", Long.class);
    }

    private Map<String, Object> getTalkReactionCounts(UUID talkId) {
        return jdbcTemplate.queryForMap("SELECT like_count, support_count FROM talk WHERE id = ?", talkId);
    }
}
