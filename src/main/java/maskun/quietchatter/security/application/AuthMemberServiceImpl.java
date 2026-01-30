package maskun.quietchatter.security.application;

import maskun.quietchatter.member.application.in.MemberQueryable;
import maskun.quietchatter.member.application.in.MemberRegistrable;
import maskun.quietchatter.member.domain.Member;
import maskun.quietchatter.security.application.in.AuthMemberNotFoundException;
import maskun.quietchatter.security.application.in.AuthMemberService;
import maskun.quietchatter.security.application.out.AuthMemberCache;
import maskun.quietchatter.security.domain.AuthMember;
import org.jspecify.annotations.NullMarked;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@NullMarked
@Service
class AuthMemberServiceImpl implements AuthMemberService {
    private final AuthMemberCache authMemberCache;
    private final MemberQueryable memberQueryable;
    private final MemberRegistrable memberRegistrable;

    AuthMemberServiceImpl(
            AuthMemberCache authMemberCache,
            MemberQueryable memberQueryable,
            MemberRegistrable memberRegistrable) {
        this.authMemberCache = authMemberCache;
        this.memberQueryable = memberQueryable;
        this.memberRegistrable = memberRegistrable;
    }

    @Override
    public Optional<AuthMember> findById(UUID id) {
        Optional<AuthMember> cachedAuthMember = authMemberCache.findById(id);
        if (cachedAuthMember.isPresent()) {
            return cachedAuthMember;
        }

        Optional<Member> foundMember = memberQueryable.findById(id);
        if (foundMember.isEmpty()) {
            return Optional.empty();
        }

        AuthMember authMember = getAuthMember(foundMember.get());
        authMemberCache.save(authMember);
        return Optional.of(authMember);
    }

    @Override
    public AuthMember findOrThrow(UUID id) throws AuthMemberNotFoundException {
        return findById(id).orElseThrow(() -> new AuthMemberNotFoundException("member not found for id: " + id));
    }

    @Override
    public AuthMember createNewGuest() {
        Member guest = memberRegistrable.createNewGuest();
        AuthMember authMember = getAuthMember(guest);
        authMemberCache.save(authMember);
        return authMember;
    }

    private AuthMember getAuthMember(Member member) {
        return new AuthMember(Objects.requireNonNull(member.getId()), member.getRole());
    }
}
