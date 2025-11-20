package maskun.quietchatter.talk.application.in;

import java.util.List;
import maskun.quietchatter.talk.domain.Talk;
import org.springframework.util.Assert;

public record RecommendTalks(List<Talk> items) {
    public static final int MAX_SIZE = 6;
    static final String ERROR_SIZE = "Recommend items must equal or less " + MAX_SIZE + " items";

    public RecommendTalks {
        Assert.notNull(items, () -> "Recommend items null 이어선 안됩니다");
        if (items.size() > MAX_SIZE) {
            throw new IllegalArgumentException(ERROR_SIZE);
        }
        items.forEach(talk -> Assert.notNull(talk, () -> "Recommend talk null 이어선 안됩니다"));

        items = List.copyOf(items);
    }
}
