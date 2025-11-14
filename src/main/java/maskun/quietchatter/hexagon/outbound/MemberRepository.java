package maskun.quietchatter.hexagon.outbound;

import java.util.Optional;
import java.util.UUID;
import maskun.quietchatter.hexagon.domain.member.Member;
import org.springframework.data.repository.Repository;

public interface MemberRepository extends Repository<Member, UUID> {
    Optional<Member> findById(UUID id);

    Member save(Member member);
}
