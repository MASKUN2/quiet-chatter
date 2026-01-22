package maskun.quietchatter.talk.adaptor.in;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;
import maskun.quietchatter.talk.domain.Talk;

/**
 * DTO for {@link Talk}
 */
record TalkResponse(
        UUID id,
        UUID bookId,
        UUID memberId,
        Instant createdAt,
        Instant timeToHidden,
        String content,
        long like_count,
        Boolean didILike,
        long support_count,
        Boolean didISupport
) implements Serializable {

}


