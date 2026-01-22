package maskun.quietchatter.security;

import java.util.List;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@NullMarked
public class AuthMemberToken extends AbstractAuthenticationToken {
    static final String ROLE_AUTH_PREFIX = "ROLE_";
    private final AuthMember authMember;

    public AuthMemberToken(AuthMember authMember) {
        super(List.of(new SimpleGrantedAuthority(ROLE_AUTH_PREFIX + authMember.role().name())));
        this.authMember = authMember;
        setAuthenticated(true);
    }

    @Override
    public @Nullable Object getCredentials() {
        return null;
    }

    @Override
    public AuthMember getPrincipal() {
        return authMember;
    }
}
