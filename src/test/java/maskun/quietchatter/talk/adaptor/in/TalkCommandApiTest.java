package maskun.quietchatter.talk.adaptor.in;

import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import com.fasterxml.jackson.databind.ObjectMapper;
import maskun.quietchatter.MockSecurityTestConfig;
import maskun.quietchatter.member.domain.Role;
import maskun.quietchatter.security.adaptor.AuthMemberToken;
import maskun.quietchatter.security.domain.AuthMember;
import maskun.quietchatter.talk.application.in.TalkCommandable;
import maskun.quietchatter.talk.application.in.TalkCreateRequest;
import maskun.quietchatter.talk.domain.Talk;
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

import java.time.Instant;
import java.util.UUID;

import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TalkCommandApi.class)
@Import(MockSecurityTestConfig.class)
@AutoConfigureRestDocs
@Tag("restdocs")
class TalkCommandApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TalkCommandable talkCommandable;

    @Test
    void createTalk() throws Exception {
        UUID memberId = UUID.randomUUID();
        UUID bookId = UUID.randomUUID();
        UUID talkId = UUID.randomUUID();
        AuthMember authMember = new AuthMember(memberId, Role.REGULAR);

        TalkCreateWebRequest request = new TalkCreateWebRequest(bookId, "Great book!", Instant.now().plusSeconds(3600));

        // Mock Talk
        Talk talk = new Talk(bookId, memberId, "Test Nickname", "Great book!");
        // Set ID
        java.lang.reflect.Field idField = maskun.quietchatter.persistence.BaseEntity.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(talk, talkId);

        given(talkCommandable.create(any(TalkCreateRequest.class))).willReturn(talk);

        mockMvc.perform(RestDocumentationRequestBuilders.post("/v1/talks")
                        .with(authentication(new AuthMemberToken(authMember)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("create-talk",
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("Talks")
                                        .description("Create a new talk")
                                        .requestFields(
                                                fieldWithPath("bookId").description("Book ID"),
                                                fieldWithPath("content").description("Talk Content"),
                                                fieldWithPath("hidden").description("Hidden Date (Instant)").optional()
                                        )
                                        .responseFields(
                                                fieldWithPath("id").description("Created Talk ID")
                                        )
                                        .requestSchema(Schema.schema("TalkCreateRequest"))
                                        .responseSchema(Schema.schema("IdResponse"))
                                        .build()
                        )
                ));
    }

    @Test
    void updateTalk() throws Exception {
        UUID memberId = UUID.randomUUID();
        UUID talkId = UUID.randomUUID();
        AuthMember authMember = new AuthMember(memberId, Role.REGULAR);

        TalkUpdateWebRequest request = new TalkUpdateWebRequest("Updated content");

        willDoNothing().given(talkCommandable).update(eq(talkId), eq(memberId), eq("Updated content"));

        mockMvc.perform(RestDocumentationRequestBuilders.put("/v1/talks/{talkId}", talkId)
                        .with(authentication(new AuthMemberToken(authMember)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent())
                .andDo(MockMvcRestDocumentationWrapper.document("update-talk",
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("Talks")
                                        .description("Update a talk")
                                        .pathParameters(
                                                parameterWithName("talkId").description("Talk ID")
                                        )
                                        .requestFields(
                                                fieldWithPath("content").description("New content")
                                        )
                                        .requestSchema(Schema.schema("TalkUpdateRequest"))
                                        .build()
                        )
                ));
    }

    @Test
    void deleteTalk() throws Exception {
        UUID memberId = UUID.randomUUID();
        UUID talkId = UUID.randomUUID();
        AuthMember authMember = new AuthMember(memberId, Role.REGULAR);

        willDoNothing().given(talkCommandable).hide(talkId, memberId);

        mockMvc.perform(RestDocumentationRequestBuilders.delete("/v1/talks/{talkId}", talkId)
                        .with(authentication(new AuthMemberToken(authMember))))
                .andExpect(status().isNoContent())
                .andDo(MockMvcRestDocumentationWrapper.document("delete-talk",
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("Talks")
                                        .description("Delete (hide) a talk")
                                        .pathParameters(
                                                parameterWithName("talkId").description("Talk ID")
                                        )
                                        .build()
                        )
                ));
    }
}
