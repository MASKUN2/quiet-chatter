package maskun.quietchatter.talk.adaptor.in;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import maskun.quietchatter.talk.domain.Talk;

/**
 * DTO for {@link Talk}
 */
record TalkResponse(
        UUID id,
        UUID bookId,
        UUID memberId,
        LocalDateTime createdAt,
        LocalDate dateToHidden,
        String content,
        long likeCount,
        Boolean didILike,
        long supportCount,
        Boolean didISupport,
        boolean isModified
) implements Serializable {

    @JsonProperty("like_count")
    public long like_count() {
        return likeCount;
    }

    @JsonProperty("support_count")
    public long support_count() {
        return supportCount;
    }

    @JsonProperty("is_modified")
    public boolean is_modified() {
        return isModified;
    }
}
