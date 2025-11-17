package maskun.quietchatter.hexagon.outbound;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import maskun.quietchatter.hexagon.domain.reaction.Reaction;
import org.springframework.data.repository.Repository;

public interface ReactionRepository extends Repository<Reaction, Long> {

    List<Reaction> findByMemberIdAndTalkIdIn(UUID memberId, Collection<UUID> talkIds);
}
