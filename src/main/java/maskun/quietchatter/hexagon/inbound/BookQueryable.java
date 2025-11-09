package maskun.quietchatter.hexagon.inbound;

import maskun.quietchatter.hexagon.application.value.Keyword;
import maskun.quietchatter.hexagon.domain.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface BookQueryable {

    Page<Book> findBy(Keyword keyword, PageRequest pageRequest);
}
