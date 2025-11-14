package maskun.quietchatter.hexagon.domain.talk;

import jakarta.persistence.Embeddable;
import java.time.Instant;

@Embeddable
public record Time(
        Instant hidden
) {
}
