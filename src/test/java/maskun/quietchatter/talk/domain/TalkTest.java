package maskun.quietchatter.talk.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TalkTest {

    @DisplayName("톡 생성 시 숨김 날짜를 지정하지 않으면 12개월 후로 설정된다")
    @Test
    void createTalkWithoutHiddenDate() {
        // given
        UUID bookId = UUID.randomUUID();
        UUID memberId = UUID.randomUUID();
        String nickname = "tester";
        String content = "content";

        // when
        Talk talk = new Talk(bookId, memberId, nickname, content);

        // then
        assertThat(talk.getDateToHidden()).isEqualTo(LocalDate.now().plusMonths(12));
        assertThat(talk.isHidden()).isFalse();
        assertThat(talk.isModified()).isFalse();
        assertThat(talk.getNickname()).isEqualTo(nickname);
    }

    @DisplayName("톡 생성 시 숨김 날짜에 null을 전달하면 12개월 후로 설정된다")
    @Test
    void createTalkWithNullHiddenDate() {
        // given
        UUID bookId = UUID.randomUUID();
        UUID memberId = UUID.randomUUID();
        String nickname = "tester";
        String content = "content";

        // when
        Talk talk = new Talk(bookId, memberId, nickname, content, null);

        // then
        assertThat(talk.getDateToHidden()).isEqualTo(LocalDate.now().plusMonths(12));
    }

    @DisplayName("톡 생성 시 숨김 날짜를 지정하면 해당 날짜로 설정된다")
    @Test
    void createTalkWithHiddenDate() {
        // given
        UUID bookId = UUID.randomUUID();
        UUID memberId = UUID.randomUUID();
        String nickname = "tester";
        String content = "content";
        LocalDate dateToHidden = LocalDate.now().plusDays(30);

        // when
        Talk talk = new Talk(bookId, memberId, nickname, content, dateToHidden);

        // then
        assertThat(talk.getDateToHidden()).isEqualTo(dateToHidden);
        assertThat(talk.isHidden()).isFalse();
        assertThat(talk.isModified()).isFalse();
        assertThat(talk.getNickname()).isEqualTo(nickname);
    }

    @DisplayName("내용이 비어있거나 공백이면 예외가 발생한다")
    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "  "})
    void validateContentEmpty(String content) {
        // given
        UUID bookId = UUID.randomUUID();
        UUID memberId = UUID.randomUUID();
        String nickname = "tester";

        // when & then
        assertThatThrownBy(() -> new Talk(bookId, memberId, nickname, content))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("내용은 비어있을 수 없습니다.");
    }

    @DisplayName("내용이 최대 길이를 초과하면 예외가 발생한다")
    @Test
    void validateContentLength() {
        // given
        UUID bookId = UUID.randomUUID();
        UUID memberId = UUID.randomUUID();
        String nickname = "tester";
        String longContent = "a".repeat(Talk.MAX_CONTENT_LENGTH + 1);

        // when & then
        assertThatThrownBy(() -> new Talk(bookId, memberId, nickname, longContent))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("초과할 수 없습니다");
    }

    @DisplayName("톡 내용을 수정하면 내용이 변경되고 숨김 날짜가 12개월 후로 갱신된다")
    @Test
    void updateContent() {
        // given
        Talk talk = new Talk(UUID.randomUUID(), UUID.randomUUID(), "tester", "old content", LocalDate.now().plusDays(1));
        String newContent = "new content";

        // when
        talk.updateContent(newContent);

        // then
        assertThat(talk.getContent()).isEqualTo(newContent);
        assertThat(talk.getDateToHidden()).isEqualTo(LocalDate.now().plusMonths(12));
    }

    @DisplayName("lastModifiedAt이 createdAt보다 앞서 있으면있으면 isModified는 true를 반환한다")
    @Test
    void isModified() {
        // given
        Talk talk = new Talk(UUID.randomUUID(), UUID.randomUUID(), "tester", "content");
        assertThat(talk.isModified()).isFalse();

        // when
        ReflectionTestUtils.setField(talk, "createdAt", LocalDateTime.now());
        ReflectionTestUtils.setField(talk, "lastModifiedAt", LocalDateTime.now().plusMinutes(1));

        // then
        assertThat(talk.isModified()).isTrue();
    }

    @DisplayName("톡을 숨기면 isHidden 속성이 true가 된다")
    @Test
    void hide() {
        // given
        Talk talk = new Talk(UUID.randomUUID(), UUID.randomUUID(), "tester", "content");

        // when
        talk.hide();

        // then
        assertThat(talk.isHidden()).isTrue();
    }
}
