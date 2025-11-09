package maskun.quietchatter.hexagon.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.UUID;
import maskun.quietchatter.hexagon.domain.Book;
import maskun.quietchatter.hexagon.domain.value.Title;
import maskun.quietchatter.hexagon.outbound.BookRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class BookServiceImplTest implements MyTestContainers {
    @Autowired
    private BookServiceImpl bookService;

    @Autowired
    private BookRepository bookRepository;

    @Test
    void save() {
        Title title = new Title("Test book");
        Book book = bookService.Save(title);
        UUID id = book.getId();
        assertNotNull(book);
        assertNotNull(id);
        assertEquals(title, book.getTitle());

        Book byId = bookRepository.findById(id).get();
        assertThat(byId).isNotNull();
    }
}
