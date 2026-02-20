package maskun.quietchatter.security.application.in;

import maskun.quietchatter.security.domain.AuthMember;

import java.util.Optional;
import java.util.UUID;

public interface AuthMemberService {
    Optional<AuthMember> findById(UUID id);

    AuthMember findOrThrow(UUID id) throws AuthMemberNotFoundException;

    NaverProfile loginWithNaver(String code, String state);

    AuthMember getByNaverId(String providerId) throws AuthMemberNotFoundException;

    AuthMember signupWithNaver(String providerId, String nickname);

    record NaverProfile(String providerId, String nickname) {}
}
