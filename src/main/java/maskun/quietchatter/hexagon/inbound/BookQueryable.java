package maskun.quietchatter.hexagon.inbound;

import java.util.List;
import java.util.UUID;
import maskun.quietchatter.hexagon.application.value.Keyword;
import maskun.quietchatter.hexagon.domain.book.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookQueryable {

    Book findBy(UUID bookId);

    Page<Book> findBy(Keyword keyword, Pageable pageRequest);

    List<Book> findBy(List<UUID> bookIds);
}
