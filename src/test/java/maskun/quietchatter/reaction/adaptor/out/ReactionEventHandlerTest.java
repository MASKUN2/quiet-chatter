package maskun.quietchatter.reaction.adaptor.out;

import java.util.List;
import maskun.quietchatter.reaction.application.in.ReactionTarget;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@SuppressWarnings({"FieldCanBeLocal", "ConstantValue"})
@ExtendWith(MockitoExtension.class)
class ReactionEventHandlerTest {
    private final int eventCount = 10_000;
    private final int batchSize = 100;
    private int expectedCallCount;

    @Mock
    private ReactionBatchWorker worker;

    private ReactionEventHandler handler;

    @BeforeEach
    void setUp() {
        handler = new ReactionEventHandler(worker, batchSize);

        expectedCallCount = eventCount / batchSize;

        if (eventCount % batchSize != 0) {
            expectedCallCount++;
        }

        List<ReactionTarget> events = Instancio.ofList(ReactionTarget.class)
                .size(eventCount)
                .create();

        for (int i = 0; i < eventCount; i++) {
            ReactionTarget target = events.get(i);

            if (i % 2 == 0) {
                handler.add(target);
            } else {
                handler.remove(target);
            }
        }

    }

    @Test
    @DisplayName("큐에 추가 및 정확히 소모가 되는지 확인")
    void queueTest() throws InterruptedException {

        handler.beginConsume();
        Thread.sleep(200L);

        Mockito.verify(worker, Mockito.times(expectedCallCount)).process(Mockito.any());
    }
}
