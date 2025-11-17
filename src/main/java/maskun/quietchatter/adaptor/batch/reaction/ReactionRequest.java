package maskun.quietchatter.adaptor.batch.reaction;

import java.util.UUID;
import maskun.quietchatter.hexagon.domain.reaction.Reaction.Type;

public record ReactionRequest(UUID talkId, UUID memberId, Type type, Action action) {

}
