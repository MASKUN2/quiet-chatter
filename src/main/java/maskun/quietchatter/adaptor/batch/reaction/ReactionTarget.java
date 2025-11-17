package maskun.quietchatter.adaptor.batch.reaction;

import java.util.UUID;
import maskun.quietchatter.hexagon.domain.reaction.Reaction.Type;

public record ReactionTarget(UUID talkId, UUID memberId, Type type) {

}
