package maskun.quietchatter.security.adaptor.in;

import maskun.quietchatter.security.AuthMember;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@NullMarked
@RestController
@RequestMapping("/api/v1/auth")
class AuthMeApi {

    @GetMapping("/me")
    AuthMeResponse me(@Nullable @AuthenticationPrincipal AuthMember authMember) {
        if (authMember == null) {
            return new AuthMeResponse(false, null, "anonymous");
        }

        return new AuthMeResponse(true, authMember.id().toString(), authMember.role().name());
    }
}
