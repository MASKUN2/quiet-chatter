package maskun.quietchatter.web;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;

import maskun.quietchatter.MockSecurityTestConfig;

@WebMvcTest(controllers = ErrorPageController.class)
@Import(MockSecurityTestConfig.class)
@AutoConfigureRestDocs
@Tag("restdocs")
class ErrorPageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("멤버 비활성화 에러 정적 페이지 정보 반환")
    void memberDeactivated() throws Exception {
        mockMvc.perform(RestDocumentationRequestBuilders.get("/errors/member-deactivated")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Member Deactivated"))
                .andExpect(jsonPath("$.detail").value("Your account has been deactivated."))
                .andDo(MockMvcRestDocumentationWrapper.document("errors-member-deactivated",
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("Errors")
                                        .description("Get information on member deactivated state")
                                        .responseFields(
                                                fieldWithPath("title").description("Error Title"),
                                                fieldWithPath("detail").description("Error Detail"),
                                                fieldWithPath("resolution").description("Resolution snippet")
                                        )
                                        .responseSchema(Schema.schema("ErrorPageResponse"))
                                        .build()
                        )
                ));
    }
}
