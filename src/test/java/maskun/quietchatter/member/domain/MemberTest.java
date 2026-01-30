package maskun.quietchatter.member.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MemberTest {

    @Test
    void newGuest() {
        Member guest = Member.newGuest();

        assertThat(guest.getStatus()).isEqualTo(Status.ACTIVE);
        assertThat(guest.getRole()).isEqualTo(Role.GUEST);
        assertThat(guest.getNickname()).isEqualTo("guest");
    }

    @Test
    void newGuest_withNickname() {
        String nickname = "FancyGuest";
        Member guest = Member.newGuest(nickname);

        assertThat(guest.getStatus()).isEqualTo(Status.ACTIVE);
        assertThat(guest.getRole()).isEqualTo(Role.GUEST);
        assertThat(guest.getNickname()).isEqualTo(nickname);
    }
}
