package maskun.quietchatter.adaptor.web.security;

import static maskun.quietchatter.hexagon.domain.member.Role.GUEST;

import java.util.List;
import lombok.RequiredArgsConstructor;
import maskun.quietchatter.hexagon.domain.member.Member;
import maskun.quietchatter.hexagon.inbound.GuestRegistrable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
@RequiredArgsConstructor
public class AuthTokenProvider implements AuthenticationProvider {
    private final GuestRegistrable guestRegistrable;

    @Override
    public Authentication getGuest() {
        Member guest = getNew();
        return new UsernamePasswordAuthenticationToken(guest.getId(), null, getGuestAuthorities());
    }

    private Member getNew() {
        Member guest = guestRegistrable.createNewGuest();
        Assert.isTrue(guest.getRole().equals(GUEST), "게스트 역할 예상하였으나 다름");
        return guest;
    }

    private static List<SimpleGrantedAuthority> getGuestAuthorities() {
        String authority = "ROLE_" + GUEST.name();
        return List.of(new SimpleGrantedAuthority(authority));
    }

}
