package maskun.quietchatter.talk.application;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import maskun.quietchatter.talk.application.in.TalkUpdatable;
import maskun.quietchatter.talk.application.out.TalkRepository;
import maskun.quietchatter.talk.domain.Talk;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
class TalkUpdateService implements TalkUpdatable {
    private final TalkRepository talkRepository;

    @Override
    @Transactional
    public void update(UUID talkId, UUID memberId, String content) {
        Talk talk = talkRepository.require(talkId);
        validateOwner(talk, memberId);
        talk.updateContent(content);
    }

    @Override
    @Transactional
    public void hide(UUID talkId, UUID memberId) {
        Talk talk = talkRepository.require(talkId);
        validateOwner(talk, memberId);
        talk.hide();
    }

    private void validateOwner(Talk talk, UUID memberId) {
        if (!talk.getMemberId().equals(memberId)) {
            throw new IllegalArgumentException("본인의 톡만 수정/삭제할 수 있습니다.");
        }
    }
}
