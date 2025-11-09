package maskun.quietchatter.hexagon.application;

import maskun.quietchatter.hexagon.application.value.Keyword;
import maskun.quietchatter.hexagon.domain.Book;
import maskun.quietchatter.hexagon.inbound.BookQueryable;
import maskun.quietchatter.hexagon.outbound.BookKeywordSearcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class BookQueryService implements BookQueryable {
    private final BookKeywordSearcher bookKeywordSearcher;

    public BookQueryService(BookKeywordSearcher bookKeywordSearcher) {
        this.bookKeywordSearcher = bookKeywordSearcher;
    }

    @Override
    public Page<Book> findBy(Keyword keyword, PageRequest pageRequest) {
        return bookKeywordSearcher.findByKeyword(keyword, pageRequest);
    }
}
