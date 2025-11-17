package maskun.quietchatter.hexagon.application;

import java.util.UUID;

public record TalkMemberReaction(UUID talkId, UUID memberId, boolean like, boolean support) {

    public TalkMemberReaction updateLike(boolean like) {
        return new TalkMemberReaction(talkId, memberId, like, support);
    }

    public TalkMemberReaction updateSupport(boolean support) {
        return new TalkMemberReaction(talkId, memberId, like, support);
    }
}
