package maskun.quietchatter.security.adaptor.in;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
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
import maskun.quietchatter.member.domain.Role;
import maskun.quietchatter.security.adaptor.AuthMemberToken;
import maskun.quietchatter.security.adaptor.AuthTokenService;
import maskun.quietchatter.security.domain.AuthMember;
import maskun.quietchatter.talk.application.in.TalkCommandable;
import maskun.quietchatter.talk.application.in.TalkQueryable;
import maskun.quietchatter.talk.domain.Talk;

@WebMvcTest(controllers = MeApi.class)
@Import(MockSecurityTestConfig.class)
@AutoConfigureRestDocs
@Tag("restdocs")
class MeApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TalkQueryable talkQueryable;

    @MockitoBean
    private TalkCommandable talkCommandable;

    @MockitoBean
    private MemberCommandable memberCommandable;

    @MockitoBean
    private AuthTokenService authTokenService;

    @Test
    @DisplayName("내 톡 목록 조회")
    void getMyTalks() throws Exception {
        UUID memberId = UUID.randomUUID();
        AuthMember authMember = new AuthMember(memberId, Role.REGULAR, maskun.quietchatter.member.domain.Status.ACTIVE);
        
        Talk talk = new Talk(UUID.randomUUID(), memberId, "testUser", "test content", LocalDate.now().plusMonths(12));
        Page<Talk> page = new PageImpl<>(List.of(talk));

        given(talkQueryable.findByMemberId(eq(memberId), any(Pageable.class))).willReturn(page);

        mockMvc.perform(RestDocumentationRequestBuilders.get("/v1/me/talks")
                        .param("page", "0")
                        .param("size", "10")
                        .with(authentication(new AuthMemberToken(authMember)))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("me-talks-get",
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("Me")
                                        .description("Get my talks")
                                        .responseSchema(Schema.schema("Page<Talk>"))
                                        .build()
                        )
                ));
    }

    @Test
    @DisplayName("닉네임 변경")
    void updateProfile() throws Exception {
        UUID memberId = UUID.randomUUID();
        AuthMember authMember = new AuthMember(memberId, Role.REGULAR, maskun.quietchatter.member.domain.Status.ACTIVE);
        
        MeApi.UpdateProfileRequest request = new MeApi.UpdateProfileRequest("newNickname");

        mockMvc.perform(RestDocumentationRequestBuilders.put("/v1/me/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(authentication(new AuthMemberToken(authMember))))
                .andExpect(status().isNoContent())
                .andDo(MockMvcRestDocumentationWrapper.document("me-profile-update",
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("Me")
                                        .description("Update profile nickname")
                                        .requestSchema(Schema.schema("UpdateProfileRequest"))
                                        .build()
                        )
                ));
    }

    @Test
    @DisplayName("회원 탈퇴")
    void withdraw() throws Exception {
        UUID memberId = UUID.randomUUID();
        AuthMember authMember = new AuthMember(memberId, Role.REGULAR, maskun.quietchatter.member.domain.Status.ACTIVE);

        mockMvc.perform(RestDocumentationRequestBuilders.delete("/v1/me")
                        .with(authentication(new AuthMemberToken(authMember))))
                .andExpect(status().isNoContent())
                .andDo(MockMvcRestDocumentationWrapper.document("me-withdraw",
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("Me")
                                        .description("Withdraw membership (Deactivate)")
                                        .build()
                        )
                ));
    }
}
