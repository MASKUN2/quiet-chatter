package maskun.quietchatter.security.adaptor;

import org.jspecify.annotations.NullMarked;
import org.springframework.boot.context.properties.ConfigurationProperties;

@NullMarked
@ConfigurationProperties("app.cookie")
public record AppCookieProperties(
        String domain,
        boolean secure,
        String sameSite

) {

    public AppCookieProperties {
        if (domain.isBlank()) {
            throw new IllegalArgumentException("domain must not be or blank");
        }
        if (sameSite.isBlank()) {
            throw new IllegalArgumentException("sameSite must not be or blank");
        }
    }
}