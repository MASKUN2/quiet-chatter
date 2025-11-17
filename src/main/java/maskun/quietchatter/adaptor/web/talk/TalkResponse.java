package maskun.quietchatter.adaptor.web.talk;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

/**
 * DTO for {@link maskun.quietchatter.hexagon.domain.talk.Talk}
 */
public record TalkResponse(
        UUID id,
        UUID bookId,
        UUID memberId,
        Instant createdAt,
        Instant timeToHidden,
        String content,
        long like_count,
        boolean didILike,
        long support_count,
        boolean didISupport
) implements Serializable {
}


