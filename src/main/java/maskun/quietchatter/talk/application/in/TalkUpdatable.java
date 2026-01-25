package maskun.quietchatter.talk.application.in;

import java.util.UUID;

public interface TalkUpdatable {
    void update(UUID talkId, UUID memberId, String content);

    void hide(UUID talkId, UUID memberId);
}
