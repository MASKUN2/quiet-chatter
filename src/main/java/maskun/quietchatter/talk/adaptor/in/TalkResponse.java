package maskun.quietchatter.talk.adaptor.in;

import com.fasterxml.jackson.annotation.JsonProperty;
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
        long likeCount,
        Boolean didILike,
        long supportCount,
        Boolean didISupport
) implements Serializable {

    @JsonProperty("like_count")
    public long like_count() {
        return likeCount;
    }

    @JsonProperty("support_count")
    public long support_count() {
        return supportCount;
    }
}


