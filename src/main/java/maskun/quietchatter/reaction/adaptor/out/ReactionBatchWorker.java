package maskun.quietchatter.reaction.adaptor.out;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import maskun.quietchatter.reaction.application.in.ReactionTarget;
import maskun.quietchatter.reaction.domain.Reaction.Type;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.IntStream;

@Component
@RequiredArgsConstructor
class ReactionBatchWorker {
    private final JdbcTemplate jdbcTemplate;

    @Transactional
    void process(ReactionRequestAggregator aggregator) {
        AffectedCountMap map = new AffectedCountMap();

        List<ReactionTarget> deleted = delete(aggregator.getDeletes());
        map.countDown(deleted);

        List<ReactionTarget> inserted = insert(aggregator.getInserts());
        map.countUp(inserted);

        updateCount(map.result);
    }

    private List<ReactionTarget> delete(List<ReactionTarget> targets) {
        if (targets.isEmpty()) {
            return targets;
        }

        int[] rowCount = deleteBatch(targets);
        return filterSuccess(targets, rowCount);
    }

    private List<ReactionTarget> insert(List<ReactionTarget> requests) {
        if (requests.isEmpty()) {
            return requests;
        }

        int[] affectedRows = insertBatch(requests);
        return filterSuccess(requests, affectedRows);
    }

    private int[] deleteBatch(List<ReactionTarget> requests) {
        String sql = "DELETE FROM reaction WHERE talk_id = ? and member_id = ? and type = ?";

        List<Object[]> batchArgs = requests.stream()
                .map(request -> new Object[]{request.talkId(), request.memberId(), request.type().name()})
                .toList();
        return jdbcTemplate.batchUpdate(sql, batchArgs);
    }

    private int[] insertBatch(List<ReactionTarget> requests) {
        String sql = "INSERT INTO reaction (id, talk_id, member_id, type, created_at, last_modified_at) VALUES (?, ?, ?, ?, now(), now())"
                + " ON CONFLICT (talk_id, member_id, type) DO NOTHING";

        List<Object[]> batchArgs = requests.stream()
                .map(request -> new Object[]{UUID.randomUUID(), request.talkId(), request.memberId(), request.type().name()})
                .toList();
        return jdbcTemplate.batchUpdate(sql, batchArgs);
    }

    private static List<ReactionTarget> filterSuccess(List<ReactionTarget> requests, int[] affectedRows) {
        return IntStream.range(0, affectedRows.length)
                .filter(i -> affectedRows[i] == 1)
                .mapToObj(requests::get)
                .toList();
    }

    private void updateCount(Map<UUID, Map<Type, Long>> result) {
        String sql = "UPDATE talk SET like_count = like_count + ?, support_count = support_count + ? WHERE id = ?";
        List<Object[]> batchArgs = result.entrySet().stream()
                .map(extractCounts()).toList();
        jdbcTemplate.batchUpdate(sql, batchArgs);
    }

    private static Function<Entry<UUID, Map<Type, Long>>, Object[]> extractCounts() {
        return e -> {
            UUID id = e.getKey();
            Map<Type, Long> diffMap = e.getValue();

            Long likeCount = diffMap.getOrDefault(Type.LIKE, 0L);
            Long supportCount = diffMap.getOrDefault(Type.SUPPORT, 0L);

            return new Object[]{likeCount, supportCount, id};
        };
    }

    @Getter
    private static class AffectedCountMap {

        private final Map<UUID, Map<Type, Long>> result = new HashMap<>();

        public void countUp(List<ReactionTarget> targets) {
            targets.forEach(target -> countUp(target.talkId(), target.type()));
        }

        public void countDown(List<ReactionTarget> targets) {
            targets.forEach(target -> countDown(target.talkId(), target.type()));
        }

        private void countUp(UUID talkId, Type type) {
            Map<Type, Long> score = result.computeIfAbsent(talkId, id -> new EnumMap<>(Type.class));
            score.merge(type, 1L, Long::sum);
        }

        private void countDown(UUID talkId, Type type) {
            Map<Type, Long> score = result.computeIfAbsent(talkId, id -> new EnumMap<>(Type.class));
            score.merge(type, -1L, Long::sum);
        }
    }
}
