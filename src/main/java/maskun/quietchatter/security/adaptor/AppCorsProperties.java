package maskun.quietchatter.security.adaptor;

import org.jspecify.annotations.NullMarked;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@NullMarked
@ConfigurationProperties("app.cors")
public record AppCorsProperties(
        List<String> allowedOrigins
) {
    public AppCorsProperties {
        if (allowedOrigins.isEmpty()) {
            throw new IllegalArgumentException("allowedOrigins must not be empty");
        }
    }
}
