package maskun.quietchatter.adaptor.naver;

import java.net.URI;
import java.util.List;
import java.util.function.Function;
import maskun.quietchatter.adaptor.naver.dto.NaverBookItem;
import maskun.quietchatter.adaptor.naver.dto.NaverBookSearchResponse;
import maskun.quietchatter.hexagon.application.value.Keyword;
import maskun.quietchatter.hexagon.domain.Book;
import maskun.quietchatter.hexagon.domain.value.Isbn;
import maskun.quietchatter.hexagon.domain.value.Title;
import maskun.quietchatter.hexagon.outbound.BookKeywordSearcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriBuilder;

@Component
class NaverBookSearcher implements BookKeywordSearcher {
    private final RestClient restClient;

    NaverBookSearcher(NaverApiEnvironment naverApiEnvironment) {
        this.restClient = RestClient.builder()
                .baseUrl("https://openapi.naver.com/v1/search/book.json")
                .defaultHeader("X-Naver-Client-Id", naverApiEnvironment.getClientId())
                .defaultHeader("X-Naver-Client-Secret", naverApiEnvironment.getClientSecret())
                .build();
    }

    @Override
    public Page<Book> findByKeyword(Keyword keyword, PageRequest pageRequest) {
        NaverBookSearchResponse response = fetch(keyword, pageRequest);

        List<Book> books = response.items().stream()
                .map(NaverBookSearcher::map).toList();

        return new PageImpl<>(books, pageRequest, response.total());
    }

    private NaverBookSearchResponse fetch(Keyword keyword, PageRequest pageRequest) {
        int pageNumber = pageRequest.getPageNumber();
        int pageSize = pageRequest.getPageSize();
        int start = (pageNumber * pageSize) + 1; // naver 는 1부터시작

        return restClient.get()
                .uri(buildSearchUri(keyword, start, pageSize))
                .retrieve()
                .body(NaverBookSearchResponse.class);
    }

    private static Function<UriBuilder, URI> buildSearchUri(Keyword keyword, int start, int display) {
        return uriBuilder -> uriBuilder
                .queryParam("query", keyword.value())
                .queryParam("start", start)
                .queryParam("display", display)
                .build();
    }

    private static Book map(NaverBookItem item) {
        Title title = new Title(item.title());
        Isbn isbn = new Isbn(item.isbn());
        return Book.newOf(title, isbn);
    }
}
