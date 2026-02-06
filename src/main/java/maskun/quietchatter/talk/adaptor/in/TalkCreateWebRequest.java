package maskun.quietchatter.talk.adaptor.in;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.util.UUID;

record TalkCreateWebRequest(
        @NotNull
        UUID bookId,
        @NotBlank
        @Size(max = 250)
        String content,
        Instant hidden
) {
}
