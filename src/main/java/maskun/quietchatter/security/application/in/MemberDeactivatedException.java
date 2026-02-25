package maskun.quietchatter.security.application.in;

public class MemberDeactivatedException extends RuntimeException {
    private final String reactivationToken;

    public MemberDeactivatedException(String reactivationToken) {
        super("Your account is currently deactivated. Please reactivate your account to use the service.");
        this.reactivationToken = reactivationToken;
    }

    public String getReactivationToken() {
        return reactivationToken;
    }
}
