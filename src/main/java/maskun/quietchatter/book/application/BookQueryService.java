package maskun.quietchatter.book.application;

import maskun.quietchatter.book.application.in.BookQueryable;
import maskun.quietchatter.book.application.in.Keyword;
import maskun.quietchatter.book.application.out.BookRepository;
import maskun.quietchatter.book.application.out.ExternalBook;
import maskun.quietchatter.book.application.out.ExternalBookSearcher;
import maskun.quietchatter.book.domain.Book;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

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

    private static Set<String> collectIsbn(Slice<ExternalBook> fetchedBooks) {
        return fetchedBooks.stream()
                .map(ExternalBook::isbn)
                .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Book> findBy(List<UUID> bookIds) {
        return bookRepository.findByIdIn(bookIds);
    }

    private static Function<Book, Book> updateAndGet(ExternalBook fetchedBook) {
        return exist -> {
            exist.update(fetchedBook.title());
            exist.updateAuthor(fetchedBook.author());
            exist.updateThumbnailImage(fetchedBook.thumbnailImage());
            exist.updateDescription(fetchedBook.description());
            exist.updateExternalLink(fetchedBook.externalLink());
            return exist;
        };
    }

    @Override
    @Transactional
    public Slice<Book> findBy(Keyword keyword, Pageable pageRequest) {
        Slice<ExternalBook> fetchedBooks = externalBookSearcher.findByKeyword(keyword, pageRequest);
        return mergeOrPersist(fetchedBooks);
    }

    private Map<TitleAndIsbn, Book> mapExistsBy(Set<String> isbns) {
        List<Book> isbnIn = bookRepository.findByIsbnIn(isbns);

        return isbnIn.stream()
                .collect(Collectors.toUnmodifiableMap(book -> new TitleAndIsbn(book.getTitle(), book.getIsbn()),
                        book -> book));
    }

    private Slice<Book> mergeOrPersist(Slice<ExternalBook> fetchedBooks) {
        Set<String> isbns = collectIsbn(fetchedBooks);
        Map<TitleAndIsbn, Book> existsMap = mapExistsBy(isbns);
        return fetchedBooks.map(updateOrSave(existsMap));
    }

    private Function<ExternalBook, Book> updateOrSave(final Map<TitleAndIsbn, Book> exists) {
        return externalBook -> {
            String title = externalBook.title();
            String isbn = externalBook.isbn();
            TitleAndIsbn key = new TitleAndIsbn(title, isbn);

            return Optional.ofNullable(exists.get(key))
                    .map(updateAndGet(externalBook))
                    .orElseGet(saveAndGet(externalBook));
        };
    }

    private Supplier<Book> saveAndGet(ExternalBook fetchedBook) {
        return () -> {
            Book book = Book.newOf(fetchedBook.title(), fetchedBook.isbn());
            book.updateAuthor(fetchedBook.author());
            book.updateThumbnailImage(fetchedBook.thumbnailImage());
            book.updateDescription(fetchedBook.description());
            book.updateExternalLink(fetchedBook.externalLink());
            return bookRepository.save(book);
        };
    }

    private record TitleAndIsbn(String title, String isbn) {
    }
}
