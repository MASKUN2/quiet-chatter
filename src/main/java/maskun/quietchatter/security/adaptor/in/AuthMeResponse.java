package maskun.quietchatter.security.adaptor.in;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
record AuthMeResponse(
        boolean isLoggedIn,
        @Nullable String id,
        String role
) {
}
