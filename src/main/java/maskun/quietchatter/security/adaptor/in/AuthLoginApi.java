package maskun.quietchatter.security.adaptor.in;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import maskun.quietchatter.member.application.in.RandomNickNameSupplier;
import maskun.quietchatter.member.domain.Status;
import maskun.quietchatter.security.adaptor.AuthTokenService;
import maskun.quietchatter.security.application.in.AuthMemberNotFoundException;
import maskun.quietchatter.security.application.in.AuthMemberService;
import maskun.quietchatter.security.application.in.AuthMemberService.NaverProfile;
import maskun.quietchatter.security.application.in.MemberDeactivatedException;
import maskun.quietchatter.security.domain.AuthMember;

@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
class AuthLoginApi {
    private final AuthMemberService authMemberService;
    private final AuthTokenService authTokenService;
    private final RandomNickNameSupplier randomNickNameSupplier;

    @PostMapping("/login/naver")
    public ResponseEntity<NaverLoginResponse> loginWithNaver(@RequestBody NaverLoginRequest request,
                                                             HttpServletResponse response) {
        NaverProfile profile = authMemberService.loginWithNaver(request.code(), request.state());

        try {
            AuthMember authMember = authMemberService.getByNaverId(profile.providerId());
            if (authMember.status() == Status.DEACTIVATED) {
                String reactivationToken = authTokenService.createReactivationToken(authMember.id());
                throw new MemberDeactivatedException(reactivationToken);
            }
            issueTokens(response, authMember);
            return ResponseEntity.ok(NaverLoginResponse.registered());
        } catch (AuthMemberNotFoundException e) {
            String registerToken = authTokenService.createRegisterToken(profile.providerId());
            String tempNickname = randomNickNameSupplier.get();
            return ResponseEntity.ok(NaverLoginResponse.notRegistered(registerToken, tempNickname));
        }
    }

    private void issueTokens(HttpServletResponse response, AuthMember authMember) {
        String accessToken = authTokenService.createNewAccessToken(authMember.id());
        String refreshToken = authTokenService.createAndSaveRefreshToken(authMember.id());

        authTokenService.putAccessToken(response, accessToken);
        authTokenService.putRefreshToken(response, refreshToken);
    }
}
