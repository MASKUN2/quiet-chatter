package maskun.quietchatter.hexagon.inbound;

import java.util.UUID;
import maskun.quietchatter.hexagon.domain.reaction.Reaction.Type;

public interface TalkReactable {
    void add(UUID talkId, UUID memberId, Type type);

    void remove(UUID talkId, UUID memberId, Type type);
}
