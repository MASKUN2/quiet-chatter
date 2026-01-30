package maskun.quietchatter.security.application.out;

import maskun.quietchatter.security.domain.AuthMember;

import java.util.Optional;
import java.util.UUID;

public interface AuthMemberCache {
    Optional<AuthMember> findById(UUID id);

    void save(AuthMember authMember);
}
