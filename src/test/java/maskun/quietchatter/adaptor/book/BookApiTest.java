package maskun.quietchatter.adaptor.book;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import maskun.quietchatter.hexagon.application.BookQueryService;
import maskun.quietchatter.hexagon.domain.book.Book;
import maskun.quietchatter.hexagon.domain.book.BookFixture;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;

@WebMvcTest(BookApi.class)
class BookApiTest {
    @Autowired
    MockMvcTester tester;
    @MockitoBean
    private BookQueryService bookQueryService;

    @Test
    void search() {
        Supplier<Book> bookSupplier = () -> BookFixture.builder().random().build();
        List<Book> books = IntStream.range(0, 10).mapToObj(i -> bookSupplier.get()).toList();

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
