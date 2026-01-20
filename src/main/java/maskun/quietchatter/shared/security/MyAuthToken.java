package maskun.quietchatter.shared.security;

import java.util.Collection;
import java.util.UUID;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

public class MyAuthToken extends AbstractAuthenticationToken {
    private final AuthMember authMember;

    public MyAuthToken(AuthMember authMember) {
        super(authMember.getAuthorities());
        this.authMember = authMember;
        setAuthenticated(true);
    }

    @Override
    public AuthMember getPrincipal() {
        return authMember;
    }

    @Override
    public Object getCredentials() {
        return null;
    }
}
