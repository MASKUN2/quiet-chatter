package maskun.quietchatter.book.application.out;

public class ExternalApiUnavailableException extends RuntimeException {
    public ExternalApiUnavailableException(String message) {
        super(message);
    }

    public ExternalApiUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
