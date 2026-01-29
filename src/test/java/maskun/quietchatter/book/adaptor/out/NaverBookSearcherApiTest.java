package maskun.quietchatter.book.adaptor.out;

import lombok.extern.slf4j.Slf4j;
import maskun.quietchatter.book.application.in.Keyword;
import maskun.quietchatter.book.application.out.ExternalBook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@Tag("external-api")
class NaverBookSearcherApiTest {

    private NaverBookSearcher naverBookSearcher;

    @BeforeEach
    void setUp() {
        String clientId = System.getenv("naver.api.client-id");
        String clientSecret = System.getenv("naver.api.client-secret");

        RestClient restClient = RestClient.builder()
                .baseUrl("https://openapi.naver.com/v1/search/book.json")
                .defaultHeader("X-Naver-Client-Id", clientId)
                .defaultHeader("X-Naver-Client-Secret", clientSecret)
                .build();

        naverBookSearcher = new NaverBookSearcher(restClient);
    }

    @Test
    @DisplayName("API 통신 테스트")
    void findByKeywordApi() {
        Keyword keyword = new Keyword("수레바퀴 아래서");
        PageRequest pageRequest = PageRequest.of(0, 10);

        Slice<ExternalBook> books = naverBookSearcher.findByKeyword(keyword, pageRequest);

        assertThat(books.getContent()).isNotEmpty();
        log.info("books: {}", books.getContent());

        assertThat(books.getContent()).allSatisfy(book -> assertThat(book.isbn()).isNotNull());
    }
}