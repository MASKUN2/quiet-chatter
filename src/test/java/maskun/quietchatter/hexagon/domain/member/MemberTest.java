package maskun.quietchatter.hexagon.domain.member;

import static org.assertj.core.api.Assertions.assertThat;

import org.instancio.Instancio;
import org.junit.jupiter.api.Test;

class MemberTest {

    @Test
    void newGuest() {
        Member guest = Member.newGuest();

        assertThat(guest.getLogin()).isNull();
        assertThat(guest.getStatus()).isEqualTo(Status.ACTIVE);
        assertThat(guest.getRole()).isEqualTo(Role.GUEST);
    }

    @Test
    void promote() {
        Member guest = Member.newGuest();

        Login login = new Login(Instancio.create(Id.class), Instancio.create(Password.class));
        guest.promote(login);

        assertThat(guest.getLogin()).isEqualTo(login);
        assertThat(guest.getStatus()).isEqualTo(Status.ACTIVE);
        assertThat(guest.getRole()).isEqualTo(Role.REGULAR);
    }
}
