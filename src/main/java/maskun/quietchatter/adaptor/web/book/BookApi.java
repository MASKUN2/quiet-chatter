package maskun.quietchatter.adaptor.web.book;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import maskun.quietchatter.hexagon.application.value.Keyword;
import maskun.quietchatter.hexagon.domain.book.Book;
import maskun.quietchatter.hexagon.inbound.BookQueryable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
class BookApi {
    private final BookQueryable bookQueryable;

    @GetMapping(params = "keyword")
    public ResponseEntity<Page<BookResponse>> search(@PageableDefault Pageable pageable,
                                                     @RequestParam(name = "keyword") String keywordValue) {
        Keyword keyword = new Keyword(keywordValue);
        Page<BookResponse> page = bookQueryable.findBy(keyword, pageable)
                .map(BookResponse::from);
        return ResponseEntity.ok(page);
    }

    @GetMapping(params = "id")
    public ResponseEntity<List<BookResponse>> getById(@RequestParam(name = "id") List<UUID> ids) {
        List<Book> books = bookQueryable.findBy(ids);
        List<BookResponse> responses = books.stream().map(BookResponse::from).toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{bookId}")
    public ResponseEntity<BookResponse> getDetail(@PathVariable(name = "bookId") UUID bookId) {
        BookResponse response = BookResponse.from(bookQueryable.findBy(bookId));
        return ResponseEntity.ok(response);
    }
}
