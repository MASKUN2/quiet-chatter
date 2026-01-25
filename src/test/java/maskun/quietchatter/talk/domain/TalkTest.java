package maskun.quietchatter.talk.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class TalkTest {

    @DisplayName("톡 생성 시 숨김 날짜를 지정하지 않으면 12개월 후로 설정된다")
    @Test
    void createTalkWithoutHiddenDate() {
        // given
        UUID bookId = UUID.randomUUID();
        UUID memberId = UUID.randomUUID();
        String content = "content";

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
        String content = "content";
        LocalDate dateToHidden = LocalDate.now().plusDays(30);

        // when
        Talk talk = new Talk(bookId, memberId, content, dateToHidden);

        // then
        assertThat(talk.getDateToHidden()).isEqualTo(dateToHidden);
        assertThat(talk.isHidden()).isFalse();
    }

    @DisplayName("내용이 비어있거나 공백이면 예외가 발생한다")
    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "  "})
    void validateContentEmpty(String content) {
        // given
        UUID bookId = UUID.randomUUID();
        UUID memberId = UUID.randomUUID();

        // when & then
        assertThatThrownBy(() -> new Talk(bookId, memberId, content))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("내용은 비어있을 수 없습니다.");
    }

    @DisplayName("내용이 최대 길이를 초과하면 예외가 발생한다")
    @Test
    void validateContentLength() {
        // given
        UUID bookId = UUID.randomUUID();
        UUID memberId = UUID.randomUUID();
        String longContent = "a".repeat(Talk.MAX_CONTENT_LENGTH + 1);

        // when & then
        assertThatThrownBy(() -> new Talk(bookId, memberId, longContent))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("초과할 수 없습니다");
    }
}
