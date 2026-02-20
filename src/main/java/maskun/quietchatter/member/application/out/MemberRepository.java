package maskun.quietchatter.member.application.out;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import maskun.quietchatter.member.domain.Member;
import maskun.quietchatter.member.domain.OauthProvider;
import org.springframework.data.repository.Repository;

public interface MemberRepository extends Repository<Member, UUID> {
    Optional<Member> findById(UUID id);

    Optional<Member> findByProviderAndProviderId(OauthProvider provider, String providerId);

    Member save(Member member);

    default Member require(UUID id) {
        return findById(id).orElseThrow(() ->
                new NoSuchElementException("찾을 수 없음 [%s] id=%s".formatted(Member.class.getSimpleName(), id)));
    }
}
