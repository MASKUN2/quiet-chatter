package maskun.quietchatter.shared.security;

import org.springframework.security.core.AuthenticationException;

public class AuthMemberNotfoundException extends AuthenticationException {

    public AuthMemberNotfoundException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public AuthMemberNotfoundException(String msg) {
        super(msg);
    }
}
