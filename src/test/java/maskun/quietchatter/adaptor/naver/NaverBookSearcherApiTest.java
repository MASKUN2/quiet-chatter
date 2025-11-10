package maskun.quietchatter.adaptor.naver;

import static org.assertj.core.api.Assertions.assertThat;

import maskun.quietchatter.hexagon.application.value.Keyword;
import maskun.quietchatter.hexagon.domain.book.Book;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

@Tag("external-api")
@SpringBootTest
@ActiveProfiles("test")
class NaverBookSearcherApiTest {

    @Autowired
    private NaverBookSearcher naverBookSearcher;

    @Test
    @DisplayName("API 통신 테스트")
    void findByKeywordApi() {

        Keyword keyword = new Keyword("수레바퀴 아래서");
        PageRequest pageRequest = PageRequest.of(0, 10);

        Page<Book> books = naverBookSearcher.findByKeyword(keyword, pageRequest);

        assertThat(books.getTotalElements()).isGreaterThan(0);
        assertThat(books.getContent()).isNotEmpty();

        assertThat(books.getContent()).allSatisfy(book -> assertThat(book.getIsbn()).isNotNull());
    }
}
