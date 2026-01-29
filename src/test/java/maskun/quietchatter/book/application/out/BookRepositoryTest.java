package maskun.quietchatter.book.application.out;

import maskun.quietchatter.WithTestContainerDatabases;
import maskun.quietchatter.book.domain.Book;
import maskun.quietchatter.persistence.BaseEntity;
import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.NoSuchElementException;

import static java.util.UUID.randomUUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.instancio.Select.fields;

@SpringBootTest
class BookRepositoryTest implements WithTestContainerDatabases {
    @Autowired
    private BookRepository repository;

    @Test
    void save() {

        Book book = Instancio.of(Book.class).ignore(fields().declaredIn(BaseEntity.class)).create();
        Book saved = repository.save(book);

        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getIsbn()).isNotNull();
        assertThat(saved.getTitle()).isNotNull();
        assertThat(saved.getCreatedAt()).isNotNull();
    }

    @Test
    void findByIsbnIn() {
    }

    @Test
    @DisplayName("없는 책에 대한 예외")
    void require() {
        assertThatThrownBy(() -> repository.require(randomUUID())).isInstanceOf(NoSuchElementException.class);

    }
}
