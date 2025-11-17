package maskun.quietchatter.hexagon.outbound;

import java.util.UUID;
import maskun.quietchatter.hexagon.domain.reaction.Reaction.Type;

public interface ReactionManipulator {

    void add(UUID talkId, UUID memberId, Type type);

    void remove(UUID talkId, UUID memberId, Type type);
}
