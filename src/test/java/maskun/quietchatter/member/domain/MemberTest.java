package maskun.quietchatter.member.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MemberTest {

    @Test
    void newGuest() {
        Member guest = Member.newGuest();

        assertThat(guest.getStatus()).isEqualTo(Status.ACTIVE);
        assertThat(guest.getRole()).isEqualTo(Role.GUEST);
    }
}
