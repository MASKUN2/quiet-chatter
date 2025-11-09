package maskun.quietchatter.adaptor.naver;

import static org.assertj.core.api.Assertions.assertThat;

import maskun.quietchatter.hexagon.application.value.Keyword;
import maskun.quietchatter.hexagon.domain.Book;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

@SpringBootTest
class NaverBookSearcherTest {
    @Autowired
    private NaverBookSearcher naverBookSearcher;

    @Test
    @DisplayName("API 통신 테스트")
    void findByKeyword() {
        Keyword keyword = new Keyword("수레바퀴 아래서");
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<Book> books = naverBookSearcher.findByKeyword(keyword, pageRequest);

        assertThat(books).isNotNull();
    }
}
