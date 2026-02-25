package maskun.quietchatter.security.adaptor.in;

import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import static org.mockito.BDDMockito.given;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import com.fasterxml.jackson.databind.ObjectMapper;

import maskun.quietchatter.MockSecurityTestConfig;
import maskun.quietchatter.member.application.in.MemberCommandable;
import maskun.quietchatter.security.adaptor.AuthTokenService;

@WebMvcTest(controllers = AuthReactivateApi.class)
@Import(MockSecurityTestConfig.class)
@AutoConfigureRestDocs
@Tag("restdocs")
class AuthReactivateApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthTokenService authTokenService;

    @MockitoBean
    private MemberCommandable memberCommandable;

    @Test
    @DisplayName("계정 재활성화")
    void reactivate() throws Exception {
        UUID memberId = UUID.randomUUID();
        String token = "valid-reactivation-token";
        
        AuthReactivateApi.ReactivateRequest request = new AuthReactivateApi.ReactivateRequest(token);

        given(authTokenService.parseReactivationToken(token)).willReturn(memberId);
        given(authTokenService.createNewAccessToken(memberId)).willReturn("newAccessToken");
        given(authTokenService.createAndSaveRefreshToken(memberId)).willReturn("newRefreshToken");

        mockMvc.perform(RestDocumentationRequestBuilders.post("/v1/auth/reactivate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("auth-reactivate",
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("Auth")
                                        .description("Reactivate Deactivated Account")
                                        .requestSchema(Schema.schema("ReactivateRequest"))
                                        .build()
                        )
                ));
    }
}
