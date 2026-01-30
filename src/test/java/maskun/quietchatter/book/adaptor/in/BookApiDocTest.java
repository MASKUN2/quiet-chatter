package maskun.quietchatter.book.adaptor.in;

import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import maskun.quietchatter.MockSecurityTestConfig;
import maskun.quietchatter.book.application.in.BookQueryable;
import maskun.quietchatter.book.domain.Book;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookApi.class)
@Import(MockSecurityTestConfig.class)
@AutoConfigureRestDocs
class BookApiDocTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookQueryable bookQueryable;

    @Test
    void getBookDetail() throws Exception {
        // given
        UUID bookId = UUID.randomUUID();
        Book book = Book.newOf("Test Book", "1234567890");
        // Reflection to set ID since it's BaseEntity
        java.lang.reflect.Field idField = maskun.quietchatter.persistence.BaseEntity.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(book, bookId);

        book.updateAuthor("Test Author");
        book.updateDescription("Test Description");
        book.updateThumbnailImage("http://example.com/image.png");
        book.updateExternalLink("http://example.com/book");

        given(bookQueryable.findBy(any(UUID.class))).willReturn(book);

        // when & then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v1/books/{bookId}", bookId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("get-book-detail",
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("Books")
                                        .description("Get detailed information about a book")
                                        .pathParameters(
                                                parameterWithName("bookId").description("The unique identifier of the book")
                                        )
                                        .responseFields(
                                                fieldWithPath("id").description("Book ID"),
                                                fieldWithPath("title").description("Book Title"),
                                                fieldWithPath("isbn").description("ISBN"),
                                                fieldWithPath("author").description("Author"),
                                                fieldWithPath("thumbnailImageUrl").description("Thumbnail Image URL"),
                                                fieldWithPath("description").description("Description"),
                                                fieldWithPath("externalLinkUrl").description("External Link URL")
                                        )
                                        .responseSchema(Schema.schema("BookResponse"))
                                        .build()
                        )
                ));
    }
}
