package maskun.quietchatter.book.application.in;

import maskun.quietchatter.book.domain.Book;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;
import java.util.UUID;

public interface BookQueryable {

    Book findBy(UUID bookId);

    Slice<Book> findBy(Keyword keyword, Pageable pageRequest);

    List<Book> findBy(List<UUID> bookIds);
}
