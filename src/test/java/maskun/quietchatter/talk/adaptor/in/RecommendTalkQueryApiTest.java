package maskun.quietchatter.talk.adaptor.in;

import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import maskun.quietchatter.MockSecurityTestConfig;
import maskun.quietchatter.talk.application.in.RecommendTalkQueryable;
import maskun.quietchatter.talk.application.in.RecommendTalks;
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

import java.util.List;
import java.util.UUID;

import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RecommendTalkQueryApi.class)
@Import(MockSecurityTestConfig.class)
@AutoConfigureRestDocs
@Tag("restdocs")
class RecommendTalkQueryApiTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RecommendTalkQueryable recommendTalkQueryable;

    @Test
    void getRecommendTalks() throws Exception {
        UUID bookId = UUID.randomUUID();
        UUID memberId = UUID.randomUUID();
        Talk talk = new Talk(bookId, memberId, "Test Nickname", "Recommend this!");
        // Set ID
        java.lang.reflect.Field idField = maskun.quietchatter.persistence.BaseEntity.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(talk, UUID.randomUUID());

        given(recommendTalkQueryable.get()).willReturn(new RecommendTalks(List.of(talk)));

        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v1/talks/recommend")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("get-recommend-talks",
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("Talks")
                                        .description("Get recommended talks")
                                        .responseFields(
                                                fieldWithPath("[].id").description("Talk ID"),
                                                fieldWithPath("[].bookId").description("Book ID"),
                                                fieldWithPath("[].memberId").description("Member ID"),
                                                fieldWithPath("[].nickname").description("Nickname"),
                                                fieldWithPath("[].createdAt").description("Created At"),
                                                fieldWithPath("[].dateToHidden").description("Date to be hidden"),
                                                fieldWithPath("[].content").description("Content"),
                                                fieldWithPath("[].likeCount").description("Like Count"),
                                                fieldWithPath("[].didILike").description("Did I Like"),
                                                fieldWithPath("[].supportCount").description("Support Count"),
                                                fieldWithPath("[].didISupport").description("Did I Support"),
                                                fieldWithPath("[].isModified").description("Is Modified"),
                                                fieldWithPath("[].like_count").description("Like Count (alias)").optional(),
                                                fieldWithPath("[].support_count").description("Support Count (alias)").optional(),
                                                fieldWithPath("[].is_modified").description("Is Modified (alias)").optional()
                                        )
                                        .responseSchema(Schema.schema("TalkListResponse"))
                                        .build()
                        )
                ));
    }
}
