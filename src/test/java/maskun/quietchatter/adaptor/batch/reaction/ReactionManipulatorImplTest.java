package maskun.quietchatter.adaptor.batch.reaction;

import java.util.List;
import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ReactionManipulatorImplTest {

    @Mock
    private ReactionBatchWorker worker;

    @Test
    @DisplayName("큐에 추가 및 소모가 되는지 확인")
    void queueTest() throws InterruptedException {
        ReactionManipulatorImpl manipulator = new ReactionManipulatorImpl(worker);

        List<ReactionRequest> requests = Instancio.ofList(ReactionRequest.class).size(10_000).create();

        for (ReactionRequest request : requests) {
            if (request.action().equals(Action.INSERT)) {
                manipulator.add(request.talkId(), request.memberId(), request.type());
                continue;
            }
            manipulator.remove(request.talkId(), request.memberId(), request.type());
        }

        manipulator.beginConsume();

        Thread.sleep(100L);

        Mockito.verify(worker, Mockito.times(100)).process(Mockito.any());
    }
}
