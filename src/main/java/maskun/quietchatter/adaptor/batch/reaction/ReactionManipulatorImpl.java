package maskun.quietchatter.adaptor.batch.reaction;

import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import lombok.RequiredArgsConstructor;
import maskun.quietchatter.hexagon.domain.reaction.Reaction.Type;
import maskun.quietchatter.hexagon.outbound.ReactionManipulator;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReactionManipulatorImpl implements ReactionManipulator {
    private final ReactionBatchWorker batchWorker;

    private final BlockingQueue<ReactionRequest> queue = new LinkedBlockingQueue<>();

    @Override
    public void add(UUID talkId, UUID memberId, Type type) {
        ReactionRequest request = new ReactionRequest(talkId, memberId, type, Action.INSERT);
        offer(request);
    }

    private void offer(ReactionRequest request) {
        boolean result = queue.offer(request);

        if (!result) {
            throw new IllegalStateException("큐가 꽉 찼습니다");
        }
    }

    @Override
    public void remove(UUID talkId, UUID memberId, Type type) {
        ReactionRequest request = new ReactionRequest(talkId, memberId, type, Action.DELETE);
        offer(request);
    }

    @PostConstruct
    public void beginConsume() {
        Thread thread = new Thread(this::collectAndExecute, "ReactionConsumer");
        thread.start();
    }

    private void collectAndExecute() {
        while (true) {
            try {
                ReactionRequest taken = queue.take();
                List<ReactionRequest> batch = new ArrayList<>();
                batch.add(taken);
                queue.drainTo(batch, 99);

                ReactionRequestAggregator aggregator = new ReactionRequestAggregator(batch);

                batchWorker.process(aggregator);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
