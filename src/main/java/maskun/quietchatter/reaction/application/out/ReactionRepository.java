package maskun.quietchatter.reaction.application.out;

import maskun.quietchatter.reaction.domain.Reaction;
import org.springframework.data.repository.Repository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface ReactionRepository extends Repository<Reaction, UUID> {

    List<Reaction> findByMemberIdAndTalkIdIn(UUID memberId, Collection<UUID> talkIds);
}
