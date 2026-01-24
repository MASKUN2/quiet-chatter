package maskun.quietchatter.talk.application.in;

import java.time.LocalDate;
import java.util.UUID;
import maskun.quietchatter.talk.domain.Content;

public record TalkCreateRequest(
        UUID bookId,
        UUID memberId,
        Content content,
        LocalDate dateToHidden
) {
}
