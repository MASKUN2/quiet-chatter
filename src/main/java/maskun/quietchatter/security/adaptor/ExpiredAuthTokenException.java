package maskun.quietchatter.security.adaptor;

class ExpiredAuthTokenException extends AuthTokenException {

    ExpiredAuthTokenException(String msg, Throwable cause) {
        super(msg, cause);
    }

    ExpiredAuthTokenException(String msg) {
        super(msg);
    }
}
