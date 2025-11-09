package maskun.quietchatter.hexagon.outbound;

import java.util.Optional;
import java.util.UUID;
import maskun.quietchatter.hexagon.domain.Book;
import org.springframework.data.repository.Repository;

public interface BookRepository extends Repository<Book, UUID> {
    Optional<Book> findById(UUID id);

    Book save(Book book);

    void delete(Book book);

    void deleteById(UUID id);
}
