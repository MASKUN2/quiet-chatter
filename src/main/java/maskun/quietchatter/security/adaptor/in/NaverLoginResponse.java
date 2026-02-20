package maskun.quietchatter.security.adaptor.in;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record NaverLoginResponse(
        boolean isRegistered,
        String registerToken,
        String tempNickname
) {
    public static NaverLoginResponse registered() {
        return new NaverLoginResponse(true, null, null);
    }

    public static NaverLoginResponse notRegistered(String registerToken, String tempNickname) {
        return new NaverLoginResponse(false, registerToken, tempNickname);
    }
}
