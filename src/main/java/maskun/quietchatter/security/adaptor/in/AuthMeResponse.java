package maskun.quietchatter.security.adaptor.in;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.UUID;

@NullMarked
record AuthMeResponse(
        boolean isLoggedIn,
        @Nullable UUID id,
        String role,
        String nickname
) {
}
