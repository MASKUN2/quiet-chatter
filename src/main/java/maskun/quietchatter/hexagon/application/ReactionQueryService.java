package maskun.quietchatter.hexagon.application;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import maskun.quietchatter.hexagon.domain.reaction.Reaction;
import maskun.quietchatter.hexagon.inbound.ReactionQueryable;
import maskun.quietchatter.hexagon.outbound.ReactionRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReactionQueryService implements ReactionQueryable {
    private final ReactionRepository reactionRepository;

    @Override
    public List<Reaction> getAllBy(UUID memberId, Collection<UUID> talkIds) {
        return reactionRepository.findByMemberIdAndTalkIdIn(memberId, talkIds);

    }
}
