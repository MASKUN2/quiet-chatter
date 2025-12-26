package maskun.quietchatter.book.application.out;

import static java.util.UUID.randomUUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.instancio.Select.fields;

import java.util.NoSuchElementException;
import maskun.quietchatter.book.domain.Book;
import maskun.quietchatter.shared.persistence.AuditableUuidEntity;
import maskun.quietchatter.shared.persistence.JpaConfig;
import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
@Import(JpaConfig.class)
class BookRepositoryTest {
    @Autowired
    private BookRepository repository;

    @Test
    void save() {

        Book book = Instancio.of(Book.class).ignore(fields().declaredIn(AuditableUuidEntity.class)).create();
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
