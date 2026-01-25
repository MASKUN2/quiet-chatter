package maskun.quietchatter.talk.application.in;

import java.time.LocalDate;
import java.util.UUID;

public record TalkCreateRequest(
        UUID bookId,
        UUID memberId,
        String content,
        LocalDate dateToHidden
) {
}
