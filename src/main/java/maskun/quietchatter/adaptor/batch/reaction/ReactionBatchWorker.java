package maskun.quietchatter.adaptor.batch.reaction;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import maskun.quietchatter.hexagon.domain.reaction.Reaction.Type;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReactionBatchWorker {
    private final JdbcTemplate jdbcTemplate;

    private static Function<Entry<UUID, Map<Type, Long>>, Object[]> extractReactionCounts() {
        return e -> {
            UUID id = e.getKey();
            Map<Type, Long> decrementsMap = e.getValue();

            Long likeCount = decrementsMap.getOrDefault(Type.LIKE, 0L);
            Long supportCount = decrementsMap.getOrDefault(Type.SUPPORT, 0L);

            return new Object[]{likeCount, supportCount, id};
        };
    }

    private static Map<UUID, Map<Type, Long>> getCountMap(List<ReactionTarget> affectedTargets) {
        return affectedTargets.stream()
                .collect(Collectors.groupingBy(ReactionTarget::talkId,
                        Collectors.groupingBy(ReactionTarget::type, Collectors.counting())));
    }

    private static List<Object[]> getObjects(List<ReactionTarget> requests) {
        return requests.stream()
                .map(convertToObjectArray())
                .toList();
    }

    private static Function<ReactionTarget, Object[]> convertToObjectArray() {
        return request -> new Object[]{request.talkId(), request.memberId(), request.type().name()};
    }

    public void process(ReactionRequestAggregator aggregator) {
        deleteBatch(aggregator.getDeletes());
        insertBatch(aggregator.getInserts());
    }

    public void deleteBatch(List<ReactionTarget> targets) {
        if (targets.isEmpty()) {
            return;
        }

        int[] deletedRowCounts = deleteReactions(targets);

        List<ReactionTarget> affectedTargets = IntStream.range(0, deletedRowCounts.length)
                .filter(i -> deletedRowCounts[i] == 1)
                .mapToObj(targets::get).toList();

        countDown(affectedTargets);

    }

    private int[] deleteReactions(List<ReactionTarget> requests) {
        String sql = "DELETE FROM reaction WHERE talk_id = ? and member_id = ? and type = ?";
        List<Object[]> batchArgs = getObjects(requests);
        return jdbcTemplate.batchUpdate(sql, batchArgs);
    }

    private void countDown(List<ReactionTarget> affectedTargets) {
        Map<UUID, Map<Type, Long>> decrements = getCountMap(affectedTargets);

        String sql = "UPDATE talk SET like_count = like_count - ?, support_count = support_count - ? WHERE id = ?";
        List<Object[]> batchArgs = decrements.entrySet().stream()
                .map(extractReactionCounts()).toList();
        jdbcTemplate.batchUpdate(sql, batchArgs);
    }

    public void insertBatch(List<ReactionTarget> requests) {
        if (requests.isEmpty()) {
            return;
        }

        String sql = "INSERT INTO reaction (talk_id, member_id, type, created_at) VALUES (?, ?, ?, now())"
                + " ON CONFLICT (talk_id, member_id, type) DO NOTHING";
        List<Object[]> batchArgs = getObjects(requests);
        int[] affectedRows = jdbcTemplate.batchUpdate(sql, batchArgs);

        List<ReactionTarget> affectedTargets = IntStream.range(0, affectedRows.length)
                .filter(i -> affectedRows[i] == 1)
                .mapToObj(requests::get).toList();

        countUp(affectedTargets);
    }

    private void countUp(List<ReactionTarget> affectedTargets) {
        Map<UUID, Map<Type, Long>> counts = getCountMap(affectedTargets);

        String sql = "UPDATE talk SET like_count = like_count + ?, support_count = support_count + ? WHERE id = ?";
        List<Object[]> batchArgs = counts.entrySet().stream()
                .map(extractReactionCounts()).toList();
        jdbcTemplate.batchUpdate(sql, batchArgs);
    }
}
