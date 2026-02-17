package maskun.quietchatter.security.adaptor;

import org.springframework.security.core.AuthenticationException;

public class AuthTokenException extends AuthenticationException {

    AuthTokenException(String msg, Throwable cause) {
        super(msg, cause);
    }

    AuthTokenException(String msg) {
        super(msg);
    }
}
