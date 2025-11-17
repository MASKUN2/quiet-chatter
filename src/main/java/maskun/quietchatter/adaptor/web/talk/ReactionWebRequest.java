package maskun.quietchatter.adaptor.web.talk;

import maskun.quietchatter.hexagon.domain.reaction.Reaction.Type;

public record ReactionWebRequest(
        Type type
) {
}
