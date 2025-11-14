package maskun.quietchatter.adaptor.web.security;

import java.util.List;
import org.springframework.security.web.util.matcher.RequestMatcher;

public interface GuestPromotion {
    List<RequestMatcher> getRequestMatchers();
}
