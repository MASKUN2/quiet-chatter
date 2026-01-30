package maskun.quietchatter.security.adaptor.in;

import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import maskun.quietchatter.MockSecurityTestConfig;
import maskun.quietchatter.member.application.in.MemberQueryable;
import maskun.quietchatter.member.domain.Member;
import maskun.quietchatter.member.domain.Role;
import maskun.quietchatter.security.adaptor.AuthMemberToken;
import maskun.quietchatter.security.domain.AuthMember;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;
import java.util.UUID;

import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.anonymous;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AuthMeApi.class)
@Import(MockSecurityTestConfig.class)
@AutoConfigureRestDocs
class AuthMeApiTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MemberQueryable memberQueryable;

    @Test
    @DisplayName("인증된 사용자 정보 조회")
    void me_Authenticated() throws Exception {
        UUID memberId = UUID.randomUUID();
        AuthMember authMember = new AuthMember(memberId, Role.REGULAR);
        Member member = Member.newGuest("testUser");

        given(memberQueryable.findById(any(UUID.class))).willReturn(Optional.of(member));

        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v1/auth/me")
                        .with(authentication(new AuthMemberToken(authMember)))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isLoggedIn").value(true))
                .andExpect(jsonPath("$.id").value(memberId.toString()))
                .andExpect(jsonPath("$.role").value("REGULAR"))
                .andExpect(jsonPath("$.nickname").value("testUser"))
                .andDo(MockMvcRestDocumentationWrapper.document("auth-me",
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("Auth")
                                        .description("Get current authenticated user info")
                                        .responseFields(
                                                fieldWithPath("isLoggedIn").description("Login status"),
                                                fieldWithPath("id").description("User ID").optional(),
                                                fieldWithPath("role").description("User Role"),
                                                fieldWithPath("nickname").description("User Nickname")
                                        )
                                        .responseSchema(Schema.schema("AuthMeResponse"))
                                        .build()
                        )
                ));
    }

    @Test
    @DisplayName("인증되지 않은 사용자 정보 조회")
    void me_Unauthenticated() throws Exception {
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v1/auth/me")
                        .with(anonymous())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isLoggedIn").value(false))
                .andExpect(jsonPath("$.id").doesNotExist())
                .andExpect(jsonPath("$.role").value("anonymous"))
                .andExpect(jsonPath("$.nickname").value("anonymous"))
                .andDo(MockMvcRestDocumentationWrapper.document("auth-me-anonymous",
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("Auth")
                                        .description("Get anonymous user info")
                                        .responseFields(
                                                fieldWithPath("isLoggedIn").description("Login status"),
                                                fieldWithPath("id").description("User ID (null for anonymous)").optional(),
                                                fieldWithPath("role").description("User Role"),
                                                fieldWithPath("nickname").description("User Nickname")
                                        )
                                        .responseSchema(Schema.schema("AuthMeResponse"))
                                        .build()
                        )
                ));
    }
}
