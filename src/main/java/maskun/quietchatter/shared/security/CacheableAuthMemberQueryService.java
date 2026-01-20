package maskun.quietchatter.shared.security;

import java.time.Duration;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import maskun.quietchatter.member.application.out.MemberRepository;
import maskun.quietchatter.member.domain.Member;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class CacheableAuthMemberQueryService implements AuthMemberQueryable {
    private final RedisTemplate<String, AuthMember> redisTemplate;
    private final MemberRepository memberRepository;
    private static final String KEY_PREFIX = "auth:member:";

    public CacheableAuthMemberQueryService(
            RedisTemplate<String, AuthMember> redisTemplate, MemberRepository memberRepository) {
        this.redisTemplate = redisTemplate;
        this.memberRepository = memberRepository;
    }

    @Override
    public Optional<AuthMember> findById(UUID id) {
        Optional<AuthMember> cachedAuthMember = getCachedAuthMember(id);
        if (cachedAuthMember.isPresent()) {
            return cachedAuthMember;
        }

        Optional<Member> foundMember = memberRepository.findById(id);
        if (foundMember.isEmpty()) {
            return Optional.empty();
        }

        AuthMember authMember = getAuthMember(foundMember.get());
        saveIntoCache(authMember);
        return Optional.of(authMember);
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
