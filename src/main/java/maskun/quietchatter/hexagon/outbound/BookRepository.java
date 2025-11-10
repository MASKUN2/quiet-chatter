package maskun.quietchatter.hexagon.outbound;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import maskun.quietchatter.hexagon.domain.book.Book;
import maskun.quietchatter.hexagon.domain.book.Isbn;
import org.springframework.data.repository.Repository;

public interface BookRepository extends Repository<Book, UUID> {
    Optional<Book> findById(UUID id);

    Book save(Book book);

    List<Book> findByIsbnIn(Collection<Isbn> isbns);
}
