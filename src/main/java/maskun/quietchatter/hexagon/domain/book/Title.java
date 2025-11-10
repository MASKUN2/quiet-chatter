package maskun.quietchatter.hexagon.domain.book;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public record Title(
        @Column(name = "title", nullable = false)
        String value
) {
}
