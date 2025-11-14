package maskun.quietchatter.adaptor.web.talk;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public record TalkPostRequest(
        UUID bookId,
        String content,
        Instant hidden
) {

    public TalkPostRequest {
        Objects.requireNonNull(bookId);
        Objects.requireNonNull(content);
    }
}
