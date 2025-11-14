package maskun.quietchatter.hexagon.domain.member;

import jakarta.persistence.Embeddable;

@Embeddable
public record Login(
        Id id,
        Password password
) {
}
