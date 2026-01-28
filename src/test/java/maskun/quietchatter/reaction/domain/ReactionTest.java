package maskun.quietchatter.reaction.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ReactionTest {

    @Test
    @DisplayName("생성자 null 검증")
    void constructor_validation() {
        UUID talkId = UUID.randomUUID();
        UUID memberId = UUID.randomUUID();
        Reaction.Type type = Reaction.Type.LIKE;

        assertThatThrownBy(() -> new Reaction(null, memberId, type))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("talkId");

        assertThatThrownBy(() -> new Reaction(talkId, null, type))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("memberId");

        assertThatThrownBy(() -> new Reaction(talkId, memberId, null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("type");
    }

    @Test
    @DisplayName("비즈니스 키 기반 동등성 검증")
    void equality_check() {
        UUID talkId = UUID.randomUUID();
        UUID memberId = UUID.randomUUID();
        Reaction.Type type = Reaction.Type.LIKE;

        Reaction reaction1 = new Reaction(talkId, memberId, type);
        Reaction reaction2 = new Reaction(talkId, memberId, type);

        assertThat(reaction1).isEqualTo(reaction2);
        assertThat(reaction1.hashCode()).isEqualTo(reaction2.hashCode());
    }

    @Test
    @DisplayName("다른 비즈니스 키를 가진 객체는 동등하지 않음")
    void inequality_check() {
        UUID talkId = UUID.randomUUID();
        UUID memberId = UUID.randomUUID();
        Reaction.Type type = Reaction.Type.LIKE;

        Reaction reaction1 = new Reaction(talkId, memberId, type);
        Reaction reaction2 = new Reaction(UUID.randomUUID(), memberId, type); // different talkId
        Reaction reaction3 = new Reaction(talkId, UUID.randomUUID(), type); // different memberId
        Reaction reaction4 = new Reaction(talkId, memberId, Reaction.Type.SUPPORT); // different type

        assertThat(reaction1).isNotEqualTo(reaction2);
        assertThat(reaction1).isNotEqualTo(reaction3);
        assertThat(reaction1).isNotEqualTo(reaction4);
    }
}
