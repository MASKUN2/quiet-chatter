package maskun.quietchatter.hexagon.application;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import maskun.quietchatter.hexagon.application.value.Keyword;
import maskun.quietchatter.hexagon.domain.book.Book;
import maskun.quietchatter.hexagon.domain.book.Isbn;
import maskun.quietchatter.hexagon.domain.book.Title;
import maskun.quietchatter.hexagon.inbound.BookQueryable;
import maskun.quietchatter.hexagon.outbound.BookRepository;
import maskun.quietchatter.hexagon.outbound.ExternalBookSearcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BookQueryService implements BookQueryable {
    private final ExternalBookSearcher externalBookSearcher;
    private final BookRepository bookRepository;

    public BookQueryService(ExternalBookSearcher externalBookSearcher, BookRepository bookRepository) {
        this.externalBookSearcher = externalBookSearcher;
        this.bookRepository = bookRepository;
    }

    @Override
    @Transactional
    public Page<Book> findBy(Keyword keyword, Pageable pageRequest) {
        Page<Book> fetchedBooks = externalBookSearcher.findByKeyword(keyword, pageRequest);
        return mergeOrPersist(fetchedBooks);
    }

    private Page<Book> mergeOrPersist(Page<Book> fetchedBooks) {
        Set<Isbn> isbns = collectIsbn(fetchedBooks);
        Map<TitleAndIsbn, Book> existsMap = mapExistsBy(isbns);
        return fetchedBooks.map(updateOrSave(existsMap));
    }

    private static Set<Isbn> collectIsbn(Streamable<Book> fetchedBooks) {
        return fetchedBooks.stream()
                .map(Book::getIsbn)
                .collect(Collectors.toUnmodifiableSet());
    }

    private Map<TitleAndIsbn, Book> mapExistsBy(Set<Isbn> isbns) {
        List<Book> isbnIn = bookRepository.findByIsbnIn(isbns);

        return isbnIn.stream()
                .collect(Collectors.toUnmodifiableMap(book -> new TitleAndIsbn(book.getTitle(), book.getIsbn()),
                        book -> book));
    }

    private Function<Book, Book> updateOrSave(final Map<TitleAndIsbn, Book> exists) {
        return book -> {
            Title title = book.getTitle();
            Isbn isbn = book.getIsbn();
            TitleAndIsbn key = new TitleAndIsbn(title, isbn);

            return Optional.ofNullable(exists.get(key))
                    .map(updateAndGet(book))
                    .orElseGet(saveAndGet(book));
        };
    }

    private static Function<Book, Book> updateAndGet(Book fecthedBook) {
        return exist -> {
            exist.update(fecthedBook.getTitle());
            exist.update(fecthedBook.getAuthor());
            exist.update(fecthedBook.getThumbnailImage());
            exist.update(fecthedBook.getDescription());
            exist.update(fecthedBook.getExternalLink());
            return exist;
        };
    }

    private Supplier<Book> saveAndGet(Book fecthedBook) {
        return () -> bookRepository.save(fecthedBook);
    }

    private record TitleAndIsbn(Title title, Isbn isbn) {
    }
}
