package maskun.quietchatter.security.adaptor.in;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import maskun.quietchatter.security.adaptor.AuthTokenService;
import maskun.quietchatter.security.application.in.AuthMemberService;
import maskun.quietchatter.security.domain.AuthMember;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import maskun.quietchatter.security.application.in.AuthMemberNotFoundException;
import maskun.quietchatter.security.application.in.AuthMemberService.NaverProfile;

@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
class AuthLoginApi {
    private final AuthMemberService authMemberService;
    private final AuthTokenService authTokenService;

    @PostMapping("/login/naver")
    public ResponseEntity<NaverLoginResponse> loginWithNaver(@RequestBody NaverLoginRequest request,
                                                             HttpServletResponse response) {
        NaverProfile profile = authMemberService.loginWithNaver(request.code(), request.state());

        try {
            AuthMember authMember = authMemberService.getByNaverId(profile.providerId());
            issueTokens(response, authMember);
            return ResponseEntity.ok(NaverLoginResponse.registered());
        } catch (AuthMemberNotFoundException e) {
            String registerToken = authTokenService.createRegisterToken(profile.providerId());
            return ResponseEntity.ok(NaverLoginResponse.notRegistered(registerToken, profile.nickname()));
        }
    }

    private void issueTokens(HttpServletResponse response, AuthMember authMember) {
        String accessToken = authTokenService.createNewAccessToken(authMember.id());
        String refreshToken = authTokenService.createAndSaveRefreshToken(authMember.id());

        authTokenService.putAccessToken(response, accessToken);
        authTokenService.putRefreshToken(response, refreshToken);
    }
}
