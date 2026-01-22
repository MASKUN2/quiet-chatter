package maskun.quietchatter.reaction.application;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import maskun.quietchatter.reaction.application.in.ReactionQueryable;
import maskun.quietchatter.reaction.application.out.ReactionRepository;
import maskun.quietchatter.reaction.domain.Reaction;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
class ReactionQueryService implements ReactionQueryable {
    private final ReactionRepository reactionRepository;

    @Override
    public List<Reaction> getAllBy(UUID memberId, Collection<UUID> talkIds) {
        if (talkIds.isEmpty()) {
            return List.of();
        }
        return reactionRepository.findByMemberIdAndTalkIdIn(memberId, talkIds);
    }
}
