package maskun.quietchatter.hexagon.outbound;

import maskun.quietchatter.hexagon.application.value.Keyword;
import maskun.quietchatter.hexagon.domain.book.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ExternalBookSearcher {
    Page<Book> findByKeyword(Keyword keyword, Pageable pageRequest);
}
