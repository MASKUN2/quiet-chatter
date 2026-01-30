package maskun.quietchatter.security.adaptor.in;

import lombok.RequiredArgsConstructor;
import maskun.quietchatter.member.application.in.MemberQueryable;
import maskun.quietchatter.member.domain.Member;
import maskun.quietchatter.security.domain.AuthMember;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@NullMarked
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/auth")
class AuthMeApi {
    private final MemberQueryable memberQueryable;

    @GetMapping("/me")
    AuthMeResponse me(@Nullable @AuthenticationPrincipal AuthMember authMember) {
        if (authMember == null) {
            return new AuthMeResponse(false, null, "anonymous", "anonymous");
        }

        Member member = memberQueryable.findById(authMember.id()).orElseThrow();
        return new AuthMeResponse(true, authMember.id(), authMember.role().name(), member.getNickname());
    }
}
