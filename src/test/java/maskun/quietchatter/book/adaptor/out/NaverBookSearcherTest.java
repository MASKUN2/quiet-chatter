package maskun.quietchatter.book.adaptor.out;

import maskun.quietchatter.book.application.in.Keyword;
import maskun.quietchatter.book.application.out.ExternalApiUnavailableException;
import maskun.quietchatter.book.application.out.ExternalBook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.match.MockRestRequestMatchers;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

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

        Slice<ExternalBook> books = naverBookSearcher.findByKeyword(keyword, pageRequest);

        assertThat(books.getContent()).isNotEmpty();

        assertThat(books.getContent()).allSatisfy(book -> assertThat(book.isbn()).isNotNull());
    }

    @Test
    @DisplayName("API 호출 실패 시 ExternalApiUnavailableException 발생")
    void findByKeywordFailed() {
        mockServer.expect(ExpectedCount.once(), MockRestRequestMatchers.anything())
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR));

        Keyword keyword = new Keyword("fail");
        PageRequest pageRequest = PageRequest.of(0, 10);

        assertThatThrownBy(() -> naverBookSearcher.findByKeyword(keyword, pageRequest))
                .isInstanceOf(ExternalApiUnavailableException.class)
                .hasMessage("Naver API is unavailable");
    }
}