package maskun.quietchatter.reaction.adaptor.in;

import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import com.fasterxml.jackson.databind.ObjectMapper;
import maskun.quietchatter.MockSecurityTestConfig;
import maskun.quietchatter.member.domain.Role;
import maskun.quietchatter.reaction.application.in.ReactionModifiable;
import maskun.quietchatter.reaction.application.in.ReactionTarget;
import maskun.quietchatter.reaction.domain.Reaction;
import maskun.quietchatter.security.adaptor.AuthMemberToken;
import maskun.quietchatter.security.domain.AuthMember;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReactionCommandApi.class)
@Import(MockSecurityTestConfig.class)
@AutoConfigureRestDocs
@Tag("restdocs")
class ReactionCommandApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ReactionModifiable reactionModifiable;

    @Test
    void addReaction() throws Exception {
        UUID memberId = UUID.randomUUID();
        UUID talkId = UUID.randomUUID();
        AuthMember authMember = new AuthMember(memberId, Role.REGULAR);
        ReactionWebRequest request = new ReactionWebRequest(talkId, Reaction.Type.LIKE);

        willDoNothing().given(reactionModifiable).add(any(ReactionTarget.class));

        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/v1/reactions")
                        .with(authentication(new AuthMemberToken(authMember)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isAccepted())
                .andDo(MockMvcRestDocumentationWrapper.document("add-reaction",
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("Reactions")
                                        .description("Add a reaction (LIKE, SUPPORT)")
                                        .requestFields(
                                                fieldWithPath("talkId").description("Talk ID"),
                                                fieldWithPath("type").description("Reaction Type (LIKE, SUPPORT)")
                                        )
                                        .requestSchema(Schema.schema("ReactionRequest"))
                                        .build()
                        )
                ));
    }

    @Test
    void removeReaction() throws Exception {
        UUID memberId = UUID.randomUUID();
        UUID talkId = UUID.randomUUID();
        AuthMember authMember = new AuthMember(memberId, Role.REGULAR);
        ReactionWebRequest request = new ReactionWebRequest(talkId, Reaction.Type.LIKE);

        willDoNothing().given(reactionModifiable).remove(any(ReactionTarget.class));

        mockMvc.perform(RestDocumentationRequestBuilders.delete("/api/v1/reactions")
                        .with(authentication(new AuthMemberToken(authMember)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isAccepted())
                .andDo(MockMvcRestDocumentationWrapper.document("remove-reaction",
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("Reactions")
                                        .description("Remove a reaction")
                                        .requestFields(
                                                fieldWithPath("talkId").description("Talk ID"),
                                                fieldWithPath("type").description("Reaction Type (LIKE, SUPPORT)")
                                        )
                                        .requestSchema(Schema.schema("ReactionRequest"))
                                        .build()
                        )
                ));
    }
}
