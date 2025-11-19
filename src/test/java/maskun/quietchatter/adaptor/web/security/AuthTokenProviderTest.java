package maskun.quietchatter.adaptor.web.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;
import static org.mockito.Mockito.when;

import maskun.quietchatter.hexagon.domain.member.Member;
import maskun.quietchatter.hexagon.domain.member.Role;
import maskun.quietchatter.hexagon.inbound.GuestRegistrable;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

@ExtendWith(MockitoExtension.class)
class AuthTokenProviderTest {

    private AuthTokenProvider authTokenProvider;

    @Mock
    private GuestRegistrable guestRegistrable;

    @BeforeEach
    void setUp() {
        when(guestRegistrable.createNewGuest()).thenReturn(getGuestFixture());

        authTokenProvider = new AuthTokenProvider(guestRegistrable);
    }

    @Test
    void getGuest() {
        Authentication auth = authTokenProvider.getGuest();

        assertThat(auth).isNotNull();
        assertThat(auth.getPrincipal()).isNotNull();
        assertThat(auth.getCredentials()).isNull();
        assertThat(auth.getAuthorities()).isNotNull();
        assertThat(auth.getAuthorities().size()).isEqualTo(1);
        assertThat(auth.getAuthorities().iterator().next().getAuthority()).isEqualTo("ROLE_GUEST");

    }

    private static Member getGuestFixture() {
        return Instancio.of(Member.class)
                .set(field(Member::getRole), Role.GUEST)
                .create();
    }
}
