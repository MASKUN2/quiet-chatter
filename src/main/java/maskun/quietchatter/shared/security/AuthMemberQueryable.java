package maskun.quietchatter.shared.security;

import java.util.Optional;
import java.util.UUID;

public interface AuthMemberQueryable {
    Optional<AuthMember> findById(UUID id);
}
