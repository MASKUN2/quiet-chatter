package maskun.quietchatter.security.internal;

import java.time.Duration;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import maskun.quietchatter.member.application.in.MemberQueryable;
import maskun.quietchatter.member.application.in.MemberRegistrable;
import maskun.quietchatter.member.domain.Member;
import maskun.quietchatter.security.AuthMember;
import maskun.quietchatter.security.AuthMemberNotFoundException;
import org.jspecify.annotations.NullMarked;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@NullMarked
@Service
class AuthMemberService {
    private final RedisTemplate<String, AuthMember> redisTemplate;
    private final MemberQueryable memberQueryable;
    private final MemberRegistrable memberRegistrable;
    private static final String KEY_PREFIX = "auth:member:";

    AuthMemberService(
            RedisTemplate<String, AuthMember> redisTemplate,
            MemberQueryable memberQueryable,
            MemberRegistrable memberRegistrable) {
        this.redisTemplate = redisTemplate;
        this.memberQueryable = memberQueryable;
        this.memberRegistrable = memberRegistrable;
    }

    public Optional<AuthMember> findById(UUID id) {
        Optional<AuthMember> cachedAuthMember = getCachedAuthMember(id);
        if (cachedAuthMember.isPresent()) {
            return cachedAuthMember;
        }

        Optional<Member> foundMember = memberQueryable.findById(id);
        if (foundMember.isEmpty()) {
            return Optional.empty();
        }

        AuthMember authMember = getAuthMember(foundMember.get());
        saveIntoCache(authMember);
        return Optional.of(authMember);
    }

    public AuthMember findOrThrow(UUID id) {
        return findById(id).orElseThrow(() -> new AuthMemberNotFoundException("member not found for id: " + id));
    }

    public AuthMember createNewGuest() {
        Member guest = memberRegistrable.createNewGuest();
        AuthMember authMember = getAuthMember(guest);
        saveIntoCache(authMember);
        return authMember;
    }

    private AuthMember getAuthMember(Member member) {
        return new AuthMember(Objects.requireNonNull(member.getId()), member.getRole());
    }

    private Optional<AuthMember> getCachedAuthMember(UUID id) {
        return Optional.ofNullable(redisTemplate.opsForValue().get(KEY_PREFIX + id));
    }

    public void saveIntoCache(AuthMember authMember) {
        redisTemplate.opsForValue().set(
                KEY_PREFIX + authMember.id(),
                authMember,
                Duration.ofHours(2)
        );
    }
}
