package maskun.quietchatter.talk.adaptor.in;

import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import maskun.quietchatter.MockSecurityTestConfig;
import maskun.quietchatter.talk.application.in.TalkQueryRequest;
import maskun.quietchatter.talk.application.in.TalkQueryable;
import maskun.quietchatter.talk.domain.Talk;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TalkQueryApi.class)
@Import(MockSecurityTestConfig.class)
@AutoConfigureRestDocs
class TalkQueryApiDocTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TalkQueryable talkQueryable;

    @MockitoBean
    private TalkResponseMapper talkResponseMapper;

    @Test
    void getTalksByBookId() throws Exception {
        UUID bookId = UUID.randomUUID();
        UUID talkId = UUID.randomUUID();
        UUID memberId = UUID.randomUUID();

        // Mock Talk (Since TalkResponseMapper is mocked, Talk object content is less important but needed for Page)
        Talk talk = new Talk(bookId, memberId, "Test Nickname", "Test content");

        given(talkQueryable.findBy(any(TalkQueryRequest.class)))
                .willReturn(new PageImpl<>(List.of(talk)));

        TalkResponse response = new TalkResponse(
                talkId, bookId, memberId, "Nickname", LocalDateTime.now(), LocalDate.now().plusDays(7),
                "Test content", 10, false, 5, true, false
        );

        given(talkResponseMapper.mapToResponse(any(org.springframework.data.domain.Page.class)))
                .willReturn(new PageImpl<>(List.of(response)));
        given(talkResponseMapper.mapToResponse(any(org.springframework.data.domain.Page.class), any()))
                .willReturn(new PageImpl<>(List.of(response)));


        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v1/talks")
                        .param("bookId", bookId.toString())
                        .param("page", "0")
                        .param("size", "10")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("get-talks-by-book",
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("Talks")
                                        .description("Get talks by book ID")
                                        .queryParameters(
                                                parameterWithName("bookId").description("Book ID"),
                                                parameterWithName("page").description("Page number").optional(),
                                                parameterWithName("size").description("Page size").optional()
                                        )
                                        .responseFields(
                                                fieldWithPath("content[].id").description("Talk ID"),
                                                fieldWithPath("content[].bookId").description("Book ID"),
                                                fieldWithPath("content[].memberId").description("Member ID"),
                                                fieldWithPath("content[].nickname").description("Nickname"),
                                                fieldWithPath("content[].createdAt").description("Created At"),
                                                fieldWithPath("content[].dateToHidden").description("Date to be hidden"),
                                                fieldWithPath("content[].content").description("Content"),
                                                fieldWithPath("content[].likeCount").description("Like Count"),
                                                fieldWithPath("content[].didILike").description("Did I Like"),
                                                fieldWithPath("content[].supportCount").description("Support Count"),
                                                fieldWithPath("content[].didISupport").description("Did I Support"),
                                                fieldWithPath("content[].isModified").description("Is Modified"),
                                                // JsonProperty methods might add these fields, need to verify or document them if they appear
                                                fieldWithPath("content[].like_count").description("Like Count (snake_case alias)").optional(),
                                                fieldWithPath("content[].support_count").description("Support Count (snake_case alias)").optional(),
                                                fieldWithPath("content[].is_modified").description("Is Modified (snake_case alias)").optional(),

                                                fieldWithPath("pageable").description("Pageable info"),
                                                fieldWithPath("totalPages").description("Total Pages"),
                                                fieldWithPath("totalElements").description("Total Elements"),
                                                fieldWithPath("last").description("Is Last"),
                                                fieldWithPath("size").description("Size"),
                                                fieldWithPath("number").description("Page Number"),
                                                fieldWithPath("sort.empty").description("Sort Empty"),
                                                fieldWithPath("sort.sorted").description("Sort Sorted"),
                                                fieldWithPath("sort.unsorted").description("Sort Unsorted"),
                                                fieldWithPath("numberOfElements").description("Number of Elements"),
                                                fieldWithPath("first").description("Is First"),
                                                fieldWithPath("empty").description("Is Empty")
                                        )
                                        .responseSchema(Schema.schema("TalkPageResponse"))
                                        .build()
                        )
                ));
    }
}
