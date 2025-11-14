package maskun.quietchatter.hexagon.domain.member;

import jakarta.persistence.Embeddable;

@Embeddable
public record Id(
        String value
) {
    public static final int MAX_LENGTH = 50;
    public static final int MIN_LENGTH = 3;
    static final String ERROR_EMPTY = "Id는 null 또는 비어 있을 수 없습니다.";
    static final String ERROR_FORMAT = "Id는 영숫자만 포함할 수 있습니다.";
    static final String ERROR_LENGTH = "Id는 %d자에서 %d 자 사이여야 합니다.".formatted(MIN_LENGTH, MAX_LENGTH);

    public Id {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(ERROR_EMPTY);
        }
        if (!value.matches("^[a-zA-Z0-9]+$")) {
            throw new IllegalArgumentException(ERROR_FORMAT);
        }
        if (value.length() < MIN_LENGTH || value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(ERROR_LENGTH);
        }
    }
}
