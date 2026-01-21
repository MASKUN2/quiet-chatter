package maskun.quietchatter.member.application.in;

import java.util.Optional;
import java.util.UUID;
import maskun.quietchatter.member.domain.Member;

public interface MemberQueryable {
    Optional<Member> findById(UUID id);
}
