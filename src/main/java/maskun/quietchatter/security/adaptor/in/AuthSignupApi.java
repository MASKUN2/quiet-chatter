package maskun.quietchatter.security.adaptor.in;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import maskun.quietchatter.security.adaptor.AuthTokenService;
import maskun.quietchatter.security.application.in.AuthMemberService;
import maskun.quietchatter.security.domain.AuthMember;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
class AuthSignupApi {
    private final AuthMemberService authMemberService;
    private final AuthTokenService authTokenService;

    @PostMapping("/signup/naver")
    public ResponseEntity<Void> signupWithNaver(@RequestBody SignupRequest request,
                                                HttpServletResponse response) {
        String providerId = authTokenService.parseRegisterToken(request.registerToken());
        
        AuthMember authMember = authMemberService.signupWithNaver(providerId, request.nickname());

        String accessToken = authTokenService.createNewAccessToken(authMember.id());
        String refreshToken = authTokenService.createAndSaveRefreshToken(authMember.id());

        authTokenService.putAccessToken(response, accessToken);
        authTokenService.putRefreshToken(response, refreshToken);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
