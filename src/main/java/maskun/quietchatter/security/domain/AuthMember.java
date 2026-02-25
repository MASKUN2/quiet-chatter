package maskun.quietchatter.security.domain;

import java.util.UUID;

import org.jspecify.annotations.NullMarked;

import maskun.quietchatter.member.domain.Role;
import maskun.quietchatter.member.domain.Status;

@NullMarked
public record AuthMember(
        UUID id,
        Role role,
        Status status
) {}
