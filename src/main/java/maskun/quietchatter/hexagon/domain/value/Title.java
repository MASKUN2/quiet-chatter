package maskun.quietchatter.hexagon.domain.value;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public record Title(
        @Column(name = "title", nullable = false, length = 255)
        String value
) {
}
