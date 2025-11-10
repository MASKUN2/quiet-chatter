package maskun.quietchatter.adaptor.web.book;

import static maskun.quietchatter.hexagon.domain.Fixture.book;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.List;
import maskun.quietchatter.adaptor.web.WebConfig;
import maskun.quietchatter.hexagon.application.BookQueryService;
import maskun.quietchatter.hexagon.domain.book.Book;
import net.datafaker.Faker;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;

@WebMvcTest(BookApi.class)
@Import(WebConfig.class)
class BookApiTest {
    @Autowired
    MockMvcTester tester;
    @MockitoBean
    private BookQueryService bookQueryService;

    @Test
    void search() {

        Faker faker = new Faker();
        List<Book> books = Instancio.ofList(book().asNew().toModel())
                .size(10)
                .create();

        PageRequest pageRequest = PageRequest.of(0, 10);

        when(bookQueryService.findBy(any(), any()))
                .thenReturn(new PageImpl<>(books, pageRequest, books.size()));

        MvcTestResult result = tester.get().uri("/api/v1/books?keyword={}&size={}&page={}", "test", 10, 0)
                .exchange();

        assertThat(result).hasStatusOk()
                .bodyJson()
                .hasPathSatisfying("$.page.totalElements", value -> assertThat(value).isEqualTo(10))
                .hasPathSatisfying("$.page.totalPages", value -> assertThat(value).isEqualTo(1))
                .hasPathSatisfying("$.page.number", value -> assertThat(value).isEqualTo(0))
                .hasPathSatisfying("$.content[*].title", value -> assertThat(value).isNotEmpty())
                .hasPathSatisfying("$.content[*].isbn", value -> assertThat(value).isNotEmpty())
                .hasPathSatisfying("$.content[*].id", value -> assertThat(value).isNotEmpty());

    }
}
