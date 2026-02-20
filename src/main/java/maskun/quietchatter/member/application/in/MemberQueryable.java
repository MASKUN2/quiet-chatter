package maskun.quietchatter.member.application.in;

import java.util.Optional;
import java.util.UUID;
import maskun.quietchatter.member.domain.Member;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface MemberQueryable {
    Optional<Member> findById(UUID id);
    Optional<Member> findByNaverId(String providerId);
}
