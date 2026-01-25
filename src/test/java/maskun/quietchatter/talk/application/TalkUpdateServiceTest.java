package maskun.quietchatter.talk.application;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

import java.util.UUID;
import maskun.quietchatter.talk.application.out.TalkRepository;
import maskun.quietchatter.talk.domain.Talk;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class TalkUpdateServiceTest {

    private TalkRepository talkRepository;
    private TalkUpdateService talkUpdateService;

    @BeforeEach
    void setUp() {
        talkRepository = mock(TalkRepository.class);
        talkUpdateService = new TalkUpdateService(talkRepository);
    }

    @DisplayName("본인의 톡 내용을 수정할 수 있다")
    @Test
    void updateTalk() {
        // given
        UUID talkId = UUID.randomUUID();
        UUID memberId = UUID.randomUUID();
        Talk talk = new Talk(UUID.randomUUID(), memberId, "old content");
        given(talkRepository.require(talkId)).willReturn(talk);

        // when
        talkUpdateService.update(talkId, memberId, "new content");

        // then
        assertThat(talk.getContent()).isEqualTo("new content");
    }

    @DisplayName("타인의 톡을 수정하려고 하면 예외가 발생한다")
    @Test
    void updateTalkByOtherMember() {
        // given
        UUID talkId = UUID.randomUUID();
        UUID ownerId = UUID.randomUUID();
        UUID otherId = UUID.randomUUID();
        Talk talk = new Talk(UUID.randomUUID(), ownerId, "content");
        given(talkRepository.require(talkId)).willReturn(talk);

        // when & then
        assertThatThrownBy(() -> talkUpdateService.update(talkId, otherId, "new content"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("본인의 톡만 수정/삭제할 수 있습니다.");
    }

    @DisplayName("본인의 톡을 숨길 수 있다")
    @Test
    void hideTalk() {
        // given
        UUID talkId = UUID.randomUUID();
        UUID memberId = UUID.randomUUID();
        Talk talk = new Talk(UUID.randomUUID(), memberId, "content");
        given(talkRepository.require(talkId)).willReturn(talk);

        // when
        talkUpdateService.hide(talkId, memberId);

        // then
        assertThat(talk.isHidden()).isTrue();
    }
}
