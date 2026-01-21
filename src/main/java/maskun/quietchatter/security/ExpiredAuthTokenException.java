package maskun.quietchatter.security;

public class ExpiredAuthTokenException extends AuthTokenException {

    public ExpiredAuthTokenException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public ExpiredAuthTokenException(String msg) {
        super(msg);
    }
}
