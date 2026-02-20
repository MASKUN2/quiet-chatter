package maskun.quietchatter.security.adaptor.in;

import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import com.fasterxml.jackson.databind.ObjectMapper;
import maskun.quietchatter.MockSecurityTestConfig;
import maskun.quietchatter.member.application.in.RandomNickNameSupplier;
import maskun.quietchatter.security.adaptor.AuthTokenService;
import maskun.quietchatter.security.application.in.AuthMemberNotFoundException;
import maskun.quietchatter.security.application.in.AuthMemberService;
import maskun.quietchatter.security.application.in.AuthMemberService.NaverProfile;
import maskun.quietchatter.security.domain.AuthMember;
import maskun.quietchatter.member.domain.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AuthLoginApi.class)
@Import(MockSecurityTestConfig.class)
@AutoConfigureRestDocs
@Tag("restdocs")
class AuthLoginApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthMemberService authMemberService;

    @MockitoBean
    private AuthTokenService authTokenService;

    @MockitoBean
    private RandomNickNameSupplier randomNickNameSupplier;

    @Test
    @DisplayName("네이버 로그인 - 미가입 회원")
    void loginWithNaver_NotRegistered() throws Exception {
        String code = "testCode";
        String state = "testState";
        String providerId = "naver123";
        String nickname = "Random User";
        String registerToken = "registerToken123";

        NaverLoginRequest request = new NaverLoginRequest(code, state);
        NaverProfile profile = new NaverProfile(providerId);

        given(authMemberService.loginWithNaver(code, state)).willReturn(profile);
        given(authMemberService.getByNaverId(providerId)).willThrow(new AuthMemberNotFoundException("Not found"));
        given(authTokenService.createRegisterToken(providerId)).willReturn(registerToken);
        given(randomNickNameSupplier.get()).willReturn(nickname);

        mockMvc.perform(RestDocumentationRequestBuilders.post("/v1/auth/login/naver")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("auth-login-naver-not-registered",
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("Auth")
                                        .description("Naver Login (Not Registered)")
                                        .requestSchema(Schema.schema("NaverLoginRequest"))
                                        .responseSchema(Schema.schema("NaverLoginResponse"))
                                        .responseFields(
                                                fieldWithPath("isRegistered").description("Is registered user"),
                                                fieldWithPath("registerToken").description("Register token for signup").optional(),
                                                fieldWithPath("tempNickname").description("Randomly generated temporary nickname").optional()
                                        )
                                        .build()
                        )
                ));
    }

    @Test
    @DisplayName("네이버 로그인 - 가입된 회원")
    void loginWithNaver_Registered() throws Exception {
        String code = "testCode";
        String state = "testState";
        String providerId = "naver123";
        UUID memberId = UUID.randomUUID();

        NaverLoginRequest request = new NaverLoginRequest(code, state);
        NaverProfile profile = new NaverProfile(providerId);
        AuthMember authMember = new AuthMember(memberId, Role.REGULAR);

        given(authMemberService.loginWithNaver(code, state)).willReturn(profile);
        given(authMemberService.getByNaverId(providerId)).willReturn(authMember);
        given(authTokenService.createNewAccessToken(memberId)).willReturn("accessToken");
        given(authTokenService.createAndSaveRefreshToken(memberId)).willReturn("refreshToken");

        mockMvc.perform(RestDocumentationRequestBuilders.post("/v1/auth/login/naver")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("auth-login-naver-registered",
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("Auth")
                                        .description("Naver Login (Registered)")
                                        .requestSchema(Schema.schema("NaverLoginRequest"))
                                        .responseSchema(Schema.schema("NaverLoginResponse"))
                                        .responseFields(
                                                fieldWithPath("isRegistered").description("Is registered user")
                                        )
                                        .build()
                        )
                ));
    }
}
