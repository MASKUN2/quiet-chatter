package maskun.quietchatter.security;

import java.util.Optional;
import java.util.UUID;

public interface AuthMemberCache {
    Optional<AuthMember> findById(UUID id);

    void save(AuthMember authMember);
}
