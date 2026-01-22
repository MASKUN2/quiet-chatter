package maskun.quietchatter.security;

import org.springframework.security.core.AuthenticationException;

public class AuthTokenException extends AuthenticationException {

    public AuthTokenException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public AuthTokenException(String msg) {
        super(msg);
    }
}
