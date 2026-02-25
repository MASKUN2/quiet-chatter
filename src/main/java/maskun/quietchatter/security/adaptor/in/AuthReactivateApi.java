package maskun.quietchatter.security.adaptor.in;

import java.util.UUID;

import org.jspecify.annotations.NullMarked;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import maskun.quietchatter.member.application.in.MemberCommandable;
import maskun.quietchatter.security.adaptor.AuthTokenService;

@NullMarked
@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/auth/reactivate")
class AuthReactivateApi {
    private final AuthTokenService authTokenService;
    private final MemberCommandable memberCommandable;

    @PostMapping
    public ResponseEntity<Void> reactivate(
            @RequestBody ReactivateRequest request,
            HttpServletResponse response) {
        // Parse token (throws AuthTokenException / ExpiredAuthTokenException if invalid/expired)
        UUID memberId = authTokenService.parseReactivationToken(request.token());

        // Activate member
        memberCommandable.activate(memberId);

        // Issue new tokens mapping to successful login
        String accessToken = authTokenService.createNewAccessToken(memberId);
        String refreshToken = authTokenService.createAndSaveRefreshToken(memberId);
        authTokenService.putAccessToken(response, accessToken);
        authTokenService.putRefreshToken(response, refreshToken);

        return ResponseEntity.ok().build();
    }

    public record ReactivateRequest(String token) {}
}
