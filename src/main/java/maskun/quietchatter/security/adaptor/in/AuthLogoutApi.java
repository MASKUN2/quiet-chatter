package maskun.quietchatter.security.adaptor.in;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import maskun.quietchatter.security.adaptor.AuthTokenService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
class AuthLogoutApi {
    private final AuthTokenService authTokenService;

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = authTokenService.extractRefreshToken(request);
        if (refreshToken != null) {
            try {
                String tokenId = authTokenService.parseRefreshTokenAndGetTokenId(refreshToken);
                authTokenService.deleteRefreshTokenById(tokenId);
            } catch (Exception ignored) {
                // 토큰 파싱 실패나 이미 삭제된 경우 등은 무시하고 로그아웃 처리 진행
            }
        }

        authTokenService.expireTokenCookies(response);

        return ResponseEntity.noContent().build();
    }
}
