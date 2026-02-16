package maskun.quietchatter.security.application.in;

import maskun.quietchatter.security.domain.AuthMember;

import java.util.Optional;
import java.util.UUID;

public interface AuthMemberService {
    Optional<AuthMember> findById(UUID id);

    AuthMember findOrThrow(UUID id) throws AuthMemberNotFoundException;

    AuthMember createNewGuest();

    AuthMember loginWithNaver(String code, String state);
}
