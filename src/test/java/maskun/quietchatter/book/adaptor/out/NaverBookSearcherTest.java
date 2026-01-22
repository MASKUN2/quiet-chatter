package maskun.quietchatter.book.adaptor.out;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import maskun.quietchatter.book.application.in.Keyword;
import maskun.quietchatter.book.domain.Book;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.match.MockRestRequestMatchers;
import org.springframework.web.client.RestClient;

class NaverBookSearcherTest {

    private NaverBookSearcher naverBookSearcher;
    private MockRestServiceServer mockServer;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder();
        mockServer = MockRestServiceServer.bindTo(builder).build();
        naverBookSearcher = new NaverBookSearcher(builder.build());
    }

    @Test
    @DisplayName("API 모킹 단위 테스트")
    void findByKeywordMockedApi() {
        mockServer.expect(ExpectedCount.once(), MockRestRequestMatchers.anything())
                .andRespond(withSuccess(
                        """
                                {
                                    "total": 1,
                                    "start": 1,
                                    "display": 1,
                                    "items": [
                                        {
                                            "title": "수레바퀴 아래서",
                                            "isbn": "9788937460500"
                                        }
                                    ]
                                }""",
                        MediaType.APPLICATION_JSON
                ));

        Keyword keyword = new Keyword("수레바퀴 아래서");
        PageRequest pageRequest = PageRequest.of(0, 10);

        Page<Book> books = naverBookSearcher.findByKeyword(keyword, pageRequest);

        assertThat(books.getTotalElements()).isEqualTo(1);
        assertThat(books.getContent()).isNotEmpty();

        assertThat(books.getContent()).allSatisfy(book -> assertThat(book.getIsbn()).isNotNull());
    }
}