package maskun.quietchatter.hexagon.outbound;

import maskun.quietchatter.hexagon.application.value.Keyword;
import maskun.quietchatter.hexagon.domain.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface BookKeywordSearcher {
    Page<Book> findByKeyword(Keyword keyword, PageRequest pageRequest);
}
