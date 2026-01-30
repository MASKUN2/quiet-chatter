package maskun.quietchatter.book.adaptor.out;

import lombok.extern.slf4j.Slf4j;
import maskun.quietchatter.book.application.in.Keyword;
import maskun.quietchatter.book.application.out.ExternalApiUnavailableException;
import maskun.quietchatter.book.application.out.ExternalBook;
import maskun.quietchatter.book.application.out.ExternalBookSearcher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.util.UriBuilder;

import java.net.URI;
import java.util.List;
import java.util.function.Function;

@Slf4j
@Component
class NaverBookSearcher implements ExternalBookSearcher {
    private final RestClient naverClient;

    NaverBookSearcher(@Qualifier("naverRestClient") RestClient naverClient) {
        this.naverClient = naverClient;
    }

    private static List<ExternalBook> map(NaverBookSearchResponse response) {
        List<NaverBookItem> items = response.items();
        return items.stream()
                .filter(item -> item.isbn() != null && !item.isbn().isBlank())
                .map(NaverBookSearcher::mapToExternalBook)
                .toList();
    }

    private NaverBookSearchResponse fetch(Keyword keyword, Pageable pageRequest) {
        int pageNumber = pageRequest.getPageNumber();
        int pageSize = pageRequest.getPageSize();
        int start = (pageNumber * pageSize) + 1; // naver 는 1부터시작

        try {
            return naverClient.get()
                    .uri(buildSearchUri(keyword, start, pageSize))
                    .retrieve()
                    .body(NaverBookSearchResponse.class);
        } catch (RestClientException e) {
            log.error("Naver API error: {}", e.getMessage(), e);
            throw new ExternalApiUnavailableException("Naver API is unavailable", e);
        }
    }

    private static Function<UriBuilder, URI> buildSearchUri(Keyword keyword, int start, int display) {
        return uriBuilder -> uriBuilder
                .queryParam("query", keyword.value())
                .queryParam("start", start)
                .queryParam("display", display)
                .build();
    }

    private static ExternalBook mapToExternalBook(NaverBookItem item) {
        return new ExternalBook(
                item.title(),
                item.isbn(),
                item.author(),
                item.image(),
                item.description(),
                item.link()
        );
    }

    @Override
    public Slice<ExternalBook> findByKeyword(Keyword keyword, Pageable pageRequest) {
        NaverBookSearchResponse response = fetch(keyword, pageRequest);
        List<ExternalBook> books = map(response);

        boolean hasNext = response.start() + response.display() <= response.total();
        return new SliceImpl<>(books, pageRequest, hasNext);
    }
}
