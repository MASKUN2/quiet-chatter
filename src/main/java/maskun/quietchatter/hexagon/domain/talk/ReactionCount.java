package maskun.quietchatter.hexagon.domain.talk;

import jakarta.persistence.Embeddable;

@Embeddable
public record ReactionCount(
        long like,
        long support
) {
    public static ReactionCount zero() {
        return new ReactionCount(0, 0);
    }
}
