package maskun.quietchatter.shared.security;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;
import java.util.UUID;
import maskun.quietchatter.member.domain.Role;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public record AuthMember(
        UUID id,
        Role role
){
    static final String ROLE_AUTHORITY_PREFIX = "ROLE_";

    public SimpleGrantedAuthority roleAuthority() {
        return new SimpleGrantedAuthority(ROLE_AUTHORITY_PREFIX + role.name());
    }

    @JsonIgnore
    public List<SimpleGrantedAuthority> getAuthorities() {
        return List.of(roleAuthority());
    }
}
