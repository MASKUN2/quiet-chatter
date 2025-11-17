package maskun.quietchatter.adaptor.batch.reaction;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ReactionRequestAggregatorTest {

    @Test
    @DisplayName("집계처리가 잘되는지 테스트")
    void aggregationTest() {
        List<ReactionRequest> requests = new ArrayList<>();

        List<ReactionRequest> inserts = Instancio.ofList(ReactionRequest.class).size(100)
                .set(field(ReactionRequest::action), Action.INSERT)
                .create();
        requests.addAll(inserts);

        List<ReactionRequest> conflicts = inserts.subList(0, 10).stream()
                .map(request -> new ReactionRequest(request.talkId(), request.memberId(), request.type(),
                        Action.DELETE))
                .toList();
        requests.addAll(conflicts);

        List<ReactionRequest> deletes = Instancio.ofList(ReactionRequest.class).size(100)
                .set(field(ReactionRequest::action), Action.DELETE)
                .create();
        requests.addAll(deletes);

        Collections.shuffle(requests);

        ReactionRequestAggregator aggregator = new ReactionRequestAggregator(requests);

        assertThat(aggregator.getInserts().size()).isEqualTo(90L); // 충돌되는 것이 제거됨
        assertThat(aggregator.getDeletes().size()).isEqualTo(100L);
    }

}
