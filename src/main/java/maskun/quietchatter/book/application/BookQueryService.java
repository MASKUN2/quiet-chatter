package maskun.quietchatter.book.application;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import maskun.quietchatter.book.application.in.BookQueryable;
import maskun.quietchatter.book.application.in.Keyword;
import maskun.quietchatter.book.application.out.BookRepository;
import maskun.quietchatter.book.application.out.ExternalBookSearcher;
import maskun.quietchatter.book.domain.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
class BookQueryService implements BookQueryable {
    private final ExternalBookSearcher externalBookSearcher;
    private final BookRepository bookRepository;

    BookQueryService(ExternalBookSearcher externalBookSearcher, BookRepository bookRepository) {
        this.externalBookSearcher = externalBookSearcher;
        this.bookRepository = bookRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Book findBy(UUID bookId) {
        return bookRepository.require(bookId);
    }

    @Override
    @Transactional
    public Page<Book> findBy(Keyword keyword, Pageable pageRequest) {
        Page<Book> fetchedBooks = externalBookSearcher.findByKeyword(keyword, pageRequest);
        return mergeOrPersist(fetchedBooks);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Book> findBy(List<UUID> bookIds) {
        return bookRepository.findByIdIn(bookIds);
    }

    private Page<Book> mergeOrPersist(Page<Book> fetchedBooks) {
        Set<String> isbns = collectIsbn(fetchedBooks);
        Map<TitleAndIsbn, Book> existsMap = mapExistsBy(isbns);
        return fetchedBooks.map(updateOrSave(existsMap));
    }

    private static Set<String> collectIsbn(Streamable<Book> fetchedBooks) {
        return fetchedBooks.stream()
                .map(Book::getIsbn)
                .collect(Collectors.toUnmodifiableSet());
    }

    private Map<TitleAndIsbn, Book> mapExistsBy(Set<String> isbns) {
        List<Book> isbnIn = bookRepository.findByIsbnIn(isbns);

        return isbnIn.stream()
                .collect(Collectors.toUnmodifiableMap(book -> new TitleAndIsbn(book.getTitle(), book.getIsbn()),
                        book -> book));
    }

    private Function<Book, Book> updateOrSave(final Map<TitleAndIsbn, Book> exists) {
        return book -> {
            String title = book.getTitle();
            String isbn = book.getIsbn();
            TitleAndIsbn key = new TitleAndIsbn(title, isbn);

            return Optional.ofNullable(exists.get(key))
                    .map(updateAndGet(book))
                    .orElseGet(saveAndGet(book));
        };
    }

    private static Function<Book, Book> updateAndGet(Book fecthedBook) {
        return exist -> {
            exist.update(fecthedBook.getTitle());
            exist.updateAuthor(fecthedBook.getAuthor());
            exist.updateThumbnailImage(fecthedBook.getThumbnailImage());
            exist.updateDescription(fecthedBook.getDescription());
            exist.updateExternalLink(fecthedBook.getExternalLink());
            return exist;
        };
    }

    private Supplier<Book> saveAndGet(Book fecthedBook) {
        return () -> bookRepository.save(fecthedBook);
    }

    private record TitleAndIsbn(String title, String isbn) {
    }
}
