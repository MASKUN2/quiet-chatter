package maskun.quietchatter.talk.application.in;

import java.util.UUID;
import maskun.quietchatter.talk.domain.Talk;

public interface TalkCommandable {
    Talk create(TalkCreateRequest request);
    void update(UUID talkId, UUID memberId, String content);
    void hide(UUID talkId, UUID memberId);
}
