package maskun.quietchatter.shared.security;

import static org.assertj.core.api.Assertions.*;
import static org.instancio.Instancio.of;
import static org.mockito.Mockito.*;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Jwts.SIG;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;
import javax.crypto.SecretKey;
import maskun.quietchatter.member.domain.Role;
import org.junit.jupiter.api.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

class JwtAuthManagerTest {

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void success() {
        SecretKey myKey = SIG.HS256.key().build();
        AuthMemberQueryable authMemberQueryable = mock(AuthMemberQueryable.class);
        UUID myId = UUID.randomUUID();

        AuthMember authMember = new AuthMember(myId, Role.GUEST);

        when(authMemberQueryable.findById(eq(myId))).thenReturn(Optional.of(authMember));
        JwtAuthManager jwtAuthManager = new JwtAuthManager(myKey, authMemberQueryable);
        String token = Jwts.builder()
                .subject(myId.toString())
                .expiration(Date.from(Instant.now().plusSeconds(10)))
                .signWith(myKey).compact();
        Authentication auth = jwtAuthManager.parse(token);

        assertThat(auth.getPrincipal()).isInstanceOf(AuthMember.class);
        assertThat(auth.getPrincipal()).isEqualTo(authMember);
        assertThat(auth.getCredentials()).isNull();
        assertThat(auth.getAuthorities()).hasSize(1);
        assertThat(auth.getAuthorities().iterator().next().getAuthority()).isEqualTo("ROLE_GUEST");
    }

    @Test
    void failForMalformToken() {
        SecretKey myKey = SIG.HS256.key().build();
        JwtAuthManager jwtAuthManager = new JwtAuthManager(myKey, mock(AuthMemberQueryable.class));

        assertThatThrownBy(()-> jwtAuthManager.parse("some wrong token"))
                .isInstanceOf(AuthenticationException.class);
    }

    @Test
    void failForNotFoundMember() {
        SecretKey myKey = SIG.HS256.key().build();
        AuthMemberQueryable authMemberQueryable = mock(AuthMemberQueryable.class);
        UUID myId = UUID.randomUUID();

        when(authMemberQueryable.findById(eq(myId))).thenReturn(Optional.empty());
        JwtAuthManager jwtAuthManager = new JwtAuthManager(myKey, authMemberQueryable);

        String token = Jwts.builder()
                .subject(myId.toString())
                .expiration(Date.from(Instant.now().plusSeconds(10)))
                .signWith(myKey).compact();

        assertThatThrownBy(()-> jwtAuthManager.parse(token))
                .isInstanceOf(AuthenticationException.class);
    }

    @Test
    void failForExpired() {
        SecretKey myKey = SIG.HS256.key().build();
        AuthMemberQueryable authMemberQueryable = mock(AuthMemberQueryable.class);
        UUID myId = UUID.randomUUID();

        when(authMemberQueryable.findById(eq(myId))).thenReturn(Optional.empty());
        JwtAuthManager jwtAuthManager = new JwtAuthManager(myKey, authMemberQueryable);

        String token = Jwts.builder()
                .subject(myId.toString())
                .expiration(Date.from(Instant.now().minusSeconds(10)))
                .signWith(myKey).compact();

        assertThatThrownBy(()-> jwtAuthManager.parse(token))
                .isInstanceOf(AuthenticationException.class);
    }
}