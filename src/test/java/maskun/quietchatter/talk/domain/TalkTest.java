package maskun.quietchatter.talk.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class TalkTest {

    @DisplayName("톡 생성 시 숨김 날짜를 지정하지 않으면 12개월 후로 설정된다")
    @Test
    void createTalkWithoutHiddenDate() {
        // given
        UUID bookId = UUID.randomUUID();
        UUID memberId = UUID.randomUUID();
        Content content = new Content("content");

        // when
        Talk talk = new Talk(bookId, memberId, content);

        // then
        assertThat(talk.getDateToHidden()).isEqualTo(LocalDate.now().plusMonths(12));
        assertThat(talk.isHidden()).isFalse();
    }

    @DisplayName("톡 생성 시 숨김 날짜를 지정하면 해당 날짜로 설정된다")
    @Test
    void createTalkWithHiddenDate() {
        // given
        UUID bookId = UUID.randomUUID();
        UUID memberId = UUID.randomUUID();
        Content content = new Content("content");
        LocalDate dateToHidden = LocalDate.now().plusDays(30);

        // when
        Talk talk = new Talk(bookId, memberId, content, dateToHidden);

        // then
        assertThat(talk.getDateToHidden()).isEqualTo(dateToHidden);
        assertThat(talk.isHidden()).isFalse();
    }
}
