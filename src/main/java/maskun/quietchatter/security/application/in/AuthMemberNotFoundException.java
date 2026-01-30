package maskun.quietchatter.security.application.in;

import org.springframework.security.core.AuthenticationException;

public class AuthMemberNotFoundException extends AuthenticationException {

    public AuthMemberNotFoundException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public AuthMemberNotFoundException(String msg) {
        super(msg);
    }
}
