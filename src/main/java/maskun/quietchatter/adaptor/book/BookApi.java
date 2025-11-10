package maskun.quietchatter.adaptor.book;

import maskun.quietchatter.hexagon.application.BookQueryService;
import maskun.quietchatter.hexagon.application.value.Keyword;
import maskun.quietchatter.hexagon.domain.book.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/books")
class BookApi {
    private final BookQueryService bookQueryService;

    BookApi(BookQueryService bookQueryService) {
        this.bookQueryService = bookQueryService;
    }

    @GetMapping
    public Page<BookResponse> search(@PageableDefault Pageable pageable,
                                     @RequestParam(name = "keyword") String keywordValue) {
        Keyword keyword = new Keyword(keywordValue);
        Page<Book> books = bookQueryService.findBy(keyword, pageable);
        return books.map(BookResponse::from);
    }
}
