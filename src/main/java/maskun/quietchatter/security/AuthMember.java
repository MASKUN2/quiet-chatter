package maskun.quietchatter.security;

import java.util.UUID;
import maskun.quietchatter.member.domain.Role;
import org.jspecify.annotations.NullMarked;

@NullMarked
public record AuthMember(
        UUID id,
        Role role
) {}
