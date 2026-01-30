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
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
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

    @MockitoBean
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

    @Test
    void searchBooks() throws Exception {
        // given
        String keyword = "Test";
        Book book = Book.newOf("Test Book", "1234567890");
        // Reflection to set ID
        java.lang.reflect.Field idField = maskun.quietchatter.persistence.BaseEntity.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(book, UUID.randomUUID());
        book.updateAuthor("Test Author");
        book.updateThumbnailImage("http://example.com/image.png");

        org.springframework.data.domain.Slice<Book> slice = new org.springframework.data.domain.SliceImpl<>(java.util.List.of(book));

        given(bookQueryable.findBy(any(maskun.quietchatter.book.application.in.Keyword.class), any(org.springframework.data.domain.Pageable.class)))
                .willReturn(slice);

        // when & then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v1/books")
                        .param("keyword", keyword)
                        .param("page", "0")
                        .param("size", "10")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("search-books",
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("Books")
                                        .description("Search books by keyword")
                                        .queryParameters(
                                                parameterWithName("keyword").description("Search keyword"),
                                                parameterWithName("page").description("Page number (0-based)").optional(),
                                                parameterWithName("size").description("Page size").optional()
                                        )
                                        .responseFields(
                                                fieldWithPath("content[].id").description("Book ID"),
                                                fieldWithPath("content[].title").description("Book Title"),
                                                fieldWithPath("content[].isbn").description("ISBN"),
                                                fieldWithPath("content[].author").description("Author"),
                                                fieldWithPath("content[].thumbnailImageUrl").description("Thumbnail Image URL"),
                                                fieldWithPath("content[].description").description("Description").optional(),
                                                fieldWithPath("content[].externalLinkUrl").description("External Link URL").optional(),
                                                fieldWithPath("pageable").description("Pageable info"),
                                                fieldWithPath("size").description("Page size"),
                                                fieldWithPath("number").description("Page number"),
                                                fieldWithPath("sort.empty").description("Sort empty"),
                                                fieldWithPath("sort.sorted").description("Sort sorted"),
                                                fieldWithPath("sort.unsorted").description("Sort unsorted"),
                                                fieldWithPath("numberOfElements").description("Number of elements"),
                                                fieldWithPath("first").description("Is first page"),
                                                fieldWithPath("last").description("Is last page"),
                                                fieldWithPath("empty").description("Is empty")
                                        )
                                        .responseSchema(Schema.schema("BookSliceResponse"))
                                        .build()
                        )
                ));
    }

    @Test
    void getBooksByIds() throws Exception {
        // given
        UUID bookId1 = UUID.randomUUID();
        UUID bookId2 = UUID.randomUUID();
        Book book1 = Book.newOf("Book 1", "111");
        Book book2 = Book.newOf("Book 2", "222");

        java.lang.reflect.Field idField = maskun.quietchatter.persistence.BaseEntity.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(book1, bookId1);
        idField.set(book2, bookId2);

        given(bookQueryable.findBy(any(java.util.List.class))).willReturn(java.util.List.of(book1, book2));

        // when & then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v1/books")
                        .param("id", bookId1.toString())
                        .param("id", bookId2.toString())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("get-books-by-ids",
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("Books")
                                        .description("Get books by IDs")
                                        .queryParameters(
                                                parameterWithName("id").description("List of Book IDs")
                                        )
                                        .responseFields(
                                                fieldWithPath("[].id").description("Book ID"),
                                                fieldWithPath("[].title").description("Book Title"),
                                                fieldWithPath("[].isbn").description("ISBN"),
                                                fieldWithPath("[].author").description("Author"),
                                                fieldWithPath("[].thumbnailImageUrl").description("Thumbnail Image URL"),
                                                fieldWithPath("[].description").description("Description").optional(),
                                                fieldWithPath("[].externalLinkUrl").description("External Link URL").optional()
                                        )
                                        .responseSchema(Schema.schema("BookListResponse"))
                                        .build()
                        )
                ));
    }
}
