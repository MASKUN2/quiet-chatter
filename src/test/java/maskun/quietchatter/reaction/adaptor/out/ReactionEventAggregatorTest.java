package maskun.quietchatter.reaction.adaptor.out;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ReactionEventAggregatorTest {

    @Test
    @DisplayName("집계처리가 잘되는지 테스트")
    void aggregationTest() {

        List<ReactionEvent> inserts = Instancio.ofList(ReactionEvent.class).size(100)
                .set(field(ReactionEvent::action), Action.INSERT)
                .create();
        List<ReactionEvent> requests = new ArrayList<>(inserts);

        List<ReactionEvent> conflicts = inserts.subList(0, 10).stream()
                .map(request -> new ReactionEvent(request.talkId(), request.memberId(), request.type(),
                        Action.DELETE))
                .toList();
        requests.addAll(conflicts);

        List<ReactionEvent> duplicates = inserts.subList(11, 20).stream()
                .map(request -> new ReactionEvent(request.talkId(), request.memberId(), request.type(),
                        request.action()))
                .toList();
        requests.addAll(duplicates);

        List<ReactionEvent> deletes = Instancio.ofList(ReactionEvent.class).size(100)
                .set(field(ReactionEvent::action), Action.DELETE)
                .create();
        requests.addAll(deletes);

        Collections.shuffle(requests);

        ReactionRequestAggregator aggregator = new ReactionRequestAggregator(requests);

        assertThat(aggregator.getInserts().size()).isEqualTo(90L); // 충돌되는 것이 제거됨
        assertThat(aggregator.getDeletes().size()).isEqualTo(100L);
    }

}
