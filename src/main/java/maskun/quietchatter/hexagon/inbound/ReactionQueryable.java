package maskun.quietchatter.hexagon.inbound;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import maskun.quietchatter.hexagon.domain.reaction.Reaction;

public interface ReactionQueryable {
    List<Reaction> getAllBy(UUID memberId, Collection<UUID> talkIds);
}
