package maskun.quietchatter.member.domain;

import jakarta.persistence.Embeddable;

@Embeddable
public record Password(String hash) {

    public Password {
        if (hash == null) {
            throw new IllegalArgumentException("해시는 null일 수 없습니다.");
        }
    }

    @Override
    public String toString() {
        return "숨겨짐";
    }
}
