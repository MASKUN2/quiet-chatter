package maskun.quietchatter.security.adaptor.in;

import org.jspecify.annotations.NullMarked;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import maskun.quietchatter.member.application.in.MemberCommandable;
import maskun.quietchatter.security.adaptor.AuthTokenService;
import maskun.quietchatter.security.domain.AuthMember;
import maskun.quietchatter.talk.application.in.TalkCommandable;
import maskun.quietchatter.talk.application.in.TalkQueryable;
import maskun.quietchatter.talk.domain.Talk;

@NullMarked
@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/me")
class MeApi {
    private final TalkQueryable talkQueryable;
    private final TalkCommandable talkCommandable;
    private final MemberCommandable memberCommandable;
    private final AuthTokenService authTokenService;

    @GetMapping("/talks")
    public ResponseEntity<Page<Talk>> getMyTalks(
            @AuthenticationPrincipal AuthMember authMember,
            Pageable pageable) {
        if (authMember == null) {
            return ResponseEntity.status(401).build();
        }
        Page<Talk> talks = talkQueryable.findByMemberId(authMember.id(), pageable);
        return ResponseEntity.ok(talks);
    }

    @PutMapping("/profile")
    public ResponseEntity<Void> updateProfile(
            @AuthenticationPrincipal AuthMember authMember,
            @RequestBody UpdateProfileRequest request) {
        if (authMember == null) {
            return ResponseEntity.status(401).build();
        }
        memberCommandable.updateNickname(authMember.id(), request.nickname());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> withdraw(
            @AuthenticationPrincipal AuthMember authMember,
            HttpServletRequest request,
            HttpServletResponse response) {
        if (authMember == null) {
            return ResponseEntity.status(401).build();
        }

        // 1. Deactivate member
        memberCommandable.deactivate(authMember.id());

        // 2. Hide all talks
        talkCommandable.hideAllByMember(authMember.id());

        // 3. Invalidate session
        String refreshToken = authTokenService.extractRefreshToken(request);
        if (refreshToken != null) {
            try {
                String tokenId = authTokenService.parseRefreshTokenAndGetTokenId(refreshToken);
                authTokenService.deleteRefreshTokenById(tokenId);
            } catch (Exception ignored) {
                // Ignore if token is already invalid
            }
        }
        authTokenService.expireTokenCookies(response);

        return ResponseEntity.noContent().build();
    }

    public record UpdateProfileRequest(String nickname) {}
}
