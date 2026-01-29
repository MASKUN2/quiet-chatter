package maskun.quietchatter.book.application.out;

import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import maskun.quietchatter.book.domain.Book;
import org.springframework.data.repository.Repository;

public interface BookRepository extends Repository<Book, UUID> {

    default Book require(UUID id) throws NoSuchElementException {
        return findById(id).orElseThrow(() ->
                new NoSuchElementException("찾을 수 없음 [%s] id=%s".formatted(Book.class.getSimpleName(), id)));
    }

    Optional<Book> findById(UUID id);

    Book save(Book book);

    List<Book> findByIsbnIn(Collection<String> isbns);

    List<Book> findByIdIn(Collection<UUID> ids);
}
