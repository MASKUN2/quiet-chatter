package maskun.quietchatter.hexagon.application.value;

import java.util.UUID;
import maskun.quietchatter.hexagon.domain.talk.Content;
import maskun.quietchatter.hexagon.domain.talk.Time;

public record TalkCreateRequest(
        UUID bookId,
        UUID authorId,
        Content content,
        Time time
) {
}
