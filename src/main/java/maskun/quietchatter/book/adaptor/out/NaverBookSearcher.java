package maskun.quietchatter.book.adaptor.out;

import java.net.URI;
import java.util.List;
import java.util.function.Function;
import maskun.quietchatter.book.application.in.Keyword;
import maskun.quietchatter.book.application.out.ExternalBookSearcher;
import maskun.quietchatter.book.domain.Author;
import maskun.quietchatter.book.domain.Book;
import maskun.quietchatter.book.domain.Description;
import maskun.quietchatter.book.domain.ExternalLink;
import maskun.quietchatter.book.domain.Isbn;
import maskun.quietchatter.book.domain.ThumbnailImage;
import maskun.quietchatter.book.domain.Title;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriBuilder;

@Component
class NaverBookSearcher implements ExternalBookSearcher {
    private final RestClient naverClient;

    NaverBookSearcher(@Qualifier("naverRestClient") RestClient naverClient) {
        this.naverClient = naverClient;
    }

    @Override
    public Page<Book> findByKeyword(Keyword keyword, Pageable pageRequest) {
        NaverBookSearchResponse response = fetch(keyword, pageRequest);
        List<Book> books = map(response);
        return new PageImpl<>(books, pageRequest, response.total());
    }

    private NaverBookSearchResponse fetch(Keyword keyword, Pageable pageRequest) {
        int pageNumber = pageRequest.getPageNumber();
        int pageSize = pageRequest.getPageSize();
        int start = (pageNumber * pageSize) + 1; // naver 는 1부터시작

        return naverClient.get()
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

    private static List<Book> map(NaverBookSearchResponse response) {
        List<NaverBookItem> items = response.items();
        return items.stream()
                .map(NaverBookSearcher::map)
                .toList();
    }

    private static Book map(NaverBookItem item) {
        Title title = new Title(item.title());
        Isbn isbn = new Isbn(item.isbn());
        Book book = Book.newOf(title, isbn);

        book.update(new Author(item.author()));
        book.update(new ThumbnailImage(item.image()));
        book.update(new Description(item.description()));
        book.update(new ExternalLink(item.link()));
        return book;
    }
}
