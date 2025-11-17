package maskun.quietchatter.adaptor.batch.reaction;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class ReactionRequestAggregator {

    private final Map<ReactionTarget, ReactionRequest> requests = new HashMap<>();

    public ReactionRequestAggregator(Collection<ReactionRequest> requests) {
        requests.forEach(this::put);
    }

    private void put(ReactionRequest request) {
        ReactionTarget target = new ReactionTarget(request.talkId(), request.memberId(), request.type());

        if (requests.containsKey(target)) {
            requests.remove(target);
            return;
        }

        requests.put(target, request);
    }

    public List<ReactionTarget> getInserts() {
        return requests.entrySet().stream()
                .filter(e -> e.getValue().action() == Action.INSERT)
                .map(Map.Entry::getKey)
                .toList();
    }

    public List<ReactionTarget> getDeletes() {
        return requests.entrySet().stream()
                .filter(e -> e.getValue().action() == Action.DELETE)
                .map(Map.Entry::getKey)
                .toList();
    }

    public Set<UUID> getTalkIds() {
        return requests.keySet().stream()
                .map(ReactionTarget::talkId)
                .collect(Collectors.toSet());
    }

}
