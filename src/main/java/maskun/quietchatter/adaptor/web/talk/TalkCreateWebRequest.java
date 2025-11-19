package maskun.quietchatter.adaptor.web.talk;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public record TalkCreateWebRequest(
        UUID bookId,
        String content,
        Instant hidden
) {

    public TalkCreateWebRequest {
        Objects.requireNonNull(bookId);
        Objects.requireNonNull(content);
    }
}
