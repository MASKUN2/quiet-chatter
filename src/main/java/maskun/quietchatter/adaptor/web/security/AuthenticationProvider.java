package maskun.quietchatter.adaptor.web.security;

import org.springframework.security.core.Authentication;

public interface AuthenticationProvider {
    Authentication getGuest();
}
