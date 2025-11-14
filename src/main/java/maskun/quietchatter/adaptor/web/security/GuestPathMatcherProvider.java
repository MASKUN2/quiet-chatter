package maskun.quietchatter.adaptor.web.security;

import java.util.List;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher.Builder;
import org.springframework.security.web.util.matcher.RequestMatcher;

@Configuration
public class GuestPathMatcherProvider implements GuestPromotion {

    @Override
    public List<RequestMatcher> getRequestMatchers() {
        Builder builder = PathPatternRequestMatcher.withDefaults();
        return List.of(
                builder.matcher(HttpMethod.POST, "/api/talks")
        );
    }
}
