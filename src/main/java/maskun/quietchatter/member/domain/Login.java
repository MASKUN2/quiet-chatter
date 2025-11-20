package maskun.quietchatter.member.domain;

import jakarta.persistence.Embeddable;

@Embeddable
public record Login(
        Id id,
        Password password
) {
}
