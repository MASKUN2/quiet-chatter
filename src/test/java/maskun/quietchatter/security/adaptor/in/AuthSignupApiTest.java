package maskun.quietchatter.security.adaptor.in;

import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import com.fasterxml.jackson.databind.ObjectMapper;
import maskun.quietchatter.MockSecurityTestConfig;
import maskun.quietchatter.security.adaptor.AuthTokenService;
import maskun.quietchatter.security.application.in.AuthMemberService;
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

@WebMvcTest(controllers = AuthSignupApi.class)
@Import(MockSecurityTestConfig.class)
@AutoConfigureRestDocs
@Tag("restdocs")
class AuthSignupApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthMemberService authMemberService;

    @MockitoBean
    private AuthTokenService authTokenService;

    @Test
    @DisplayName("네이버 회원가입")
    void signupWithNaver() throws Exception {
        String nickname = "NewUser";
        String registerToken = "validRegisterToken";
        String providerId = "naver123";
        UUID memberId = UUID.randomUUID();

        SignupRequest request = new SignupRequest(nickname, registerToken);
        AuthMember authMember = new AuthMember(memberId, Role.REGULAR);

        given(authTokenService.parseRegisterToken(registerToken)).willReturn(providerId);
        given(authMemberService.signupWithNaver(providerId, nickname)).willReturn(authMember);
        given(authTokenService.createNewAccessToken(memberId)).willReturn("accessToken");
        given(authTokenService.createAndSaveRefreshToken(memberId)).willReturn("refreshToken");

        mockMvc.perform(RestDocumentationRequestBuilders.post("/v1/auth/signup/naver")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andDo(MockMvcRestDocumentationWrapper.document("auth-signup-naver",
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("Auth")
                                        .description("Naver Signup")
                                        .requestSchema(Schema.schema("SignupRequest"))
                                        .requestFields(
                                                fieldWithPath("nickname").description("Desired Nickname"),
                                                fieldWithPath("registerToken").description("Register Token from Login API")
                                        )
                                        .build()
                        )
                ));
    }
}
