package maskun.quietchatter.talk.application.in;

import java.util.List;
import maskun.quietchatter.talk.domain.Talk;
import org.springframework.util.Assert;

public record RecommendTalks(List<Talk> items) {
    public static final int MAX_SIZE = 6;
    static final String ERROR_SIZE = "Recommend items must be equal or less than " + MAX_SIZE + " items";

    public RecommendTalks {
        Assert.notNull(items, () -> "Recommend items must not be null");
        if (items.size() > MAX_SIZE) {
            throw new IllegalArgumentException(ERROR_SIZE);
        }
        items.forEach(talk -> Assert.notNull(talk, () -> "Recommend talk must not be null"));

        items = List.copyOf(items);
    }
}
