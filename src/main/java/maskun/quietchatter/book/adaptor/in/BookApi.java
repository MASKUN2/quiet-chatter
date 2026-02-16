package maskun.quietchatter.book.adaptor.in;

import lombok.RequiredArgsConstructor;
import maskun.quietchatter.book.application.in.BookQueryable;
import maskun.quietchatter.book.application.in.Keyword;
import maskun.quietchatter.book.domain.Book;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/books")
@RequiredArgsConstructor
class BookApi {
    private final BookQueryable bookQueryable;

    @GetMapping(params = "keyword")
    public ResponseEntity<Slice<BookResponse>> search(@PageableDefault Pageable pageable,
                                                     @RequestParam(name = "keyword") String keywordValue) {
        Keyword keyword = new Keyword(keywordValue);
        Slice<BookResponse> slice = bookQueryable.findBy(keyword, pageable)
                .map(BookResponse::from);
        return ResponseEntity.ok(slice);
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
