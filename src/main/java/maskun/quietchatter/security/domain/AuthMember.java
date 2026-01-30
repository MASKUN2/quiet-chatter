package maskun.quietchatter.security.domain;

import maskun.quietchatter.member.domain.Role;
import org.jspecify.annotations.NullMarked;

import java.util.UUID;

@NullMarked
public record AuthMember(
        UUID id,
        Role role
) {}
