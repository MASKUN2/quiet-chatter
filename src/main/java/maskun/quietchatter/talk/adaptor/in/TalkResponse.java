package maskun.quietchatter.talk.adaptor.in;

import com.fasterxml.jackson.annotation.JsonProperty;
import maskun.quietchatter.talk.domain.Talk;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for {@link Talk}
 */
record TalkResponse(
        UUID id,
        UUID bookId,
        UUID memberId,
        String nickname,
        LocalDateTime createdAt,
        LocalDate dateToHidden,
        String content,
        long likeCount,
        boolean didILike,
        long supportCount,
        boolean didISupport,
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
