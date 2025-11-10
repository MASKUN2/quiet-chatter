package maskun.quietchatter.hexagon.domain.book;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.util.Objects;

@Embeddable
public record Isbn(
        @Column(name = "isbn", nullable = false, length = 25)
        String value
) {

    public Isbn {
        Objects.requireNonNull(value, "ISBN 값은 null일 수 없습니다.");
    }
}
