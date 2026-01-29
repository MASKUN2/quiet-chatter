package maskun.quietchatter.book.adaptor.in;

import maskun.quietchatter.book.application.in.BookQueryable;
import maskun.quietchatter.book.domain.Book;
import maskun.quietchatter.web.WebConfig;
import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.SliceImpl;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebMvcTest(controllers = BookApi.class,
        excludeAutoConfiguration = SecurityAutoConfiguration.class)
@Import(WebConfig.class)
class BookApiTest {
    @Autowired
    MockMvcTester tester;
    @MockitoBean
    private BookQueryable bookQueryable;

    @Test
    @DisplayName("첵조회 페이지 테스트")
    void search() {
        List<Book> books = Instancio.ofList(Book.class)
                .size(10)
                .set(field(Book::getIsbn), "1234567890")
                .create();

        PageRequest pageRequest = PageRequest.of(0, 10);

        when(bookQueryable.findBy(any(), any()))
                .thenReturn(new SliceImpl<>(books, pageRequest, false));

        MvcTestResult result = tester.get().uri("/api/v1/books?keyword={}&size={}&page={}", "test", 10, 0)
                .exchange();

        assertThat(result).hasStatusOk()
                .bodyJson()
                .hasPathSatisfying("$.size", value -> assertThat(value).isEqualTo(10))
                .hasPathSatisfying("$.number", value -> assertThat(value).isEqualTo(0))
                .hasPathSatisfying("$.content[*].title", value -> assertThat(value).isNotEmpty())
                .hasPathSatisfying("$.content[*].isbn", value -> assertThat(value).isNotEmpty())
                .hasPathSatisfying("$.content[*].id", value -> assertThat(value).isNotEmpty());

    }
}
