package maskun.quietchatter.hexagon.domain.book;

import java.lang.reflect.Field;
import java.util.UUID;
import net.datafaker.Faker;
import org.springframework.util.ReflectionUtils;

public class BookFixture {
    private static final Faker faker = new Faker();

    private BookFixture() {
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(Book book) {
        return new Builder(book);
    }

    public static class Builder {
        private final Book book;

        public Builder() {
            book = new Book();
        }

        public Builder(Book book) {
            this.book = book;
        }

        public Builder random() {
            randomId();
            randomTitle();
            randomIsbn13();
            return this;
        }

        public Builder randomTitle() {
            String value = faker.book().title();
            Title title = new Title(value);
            return title(title);
        }

        public Builder title(Title title) {
            book.update(title);
            return this;
        }

        public Builder randomIsbn13() {
            String value = faker.code().isbn13();
            return isbn(new Isbn(value));
        }

        public Builder isbn(Isbn isbn) {
            book.update(isbn);
            return this;
        }

        public Builder randomId() {
            return id(UUID.randomUUID());
        }

        public Builder id(UUID uuid) {
            Field id = ReflectionUtils.findField(Book.class, "id");
            ReflectionUtils.makeAccessible(id);
            ReflectionUtils.setField(id, book, uuid);
            return this;
        }

        public Builder randomIsbn10() {
            String value = faker.code().isbn10();
            return isbn(new Isbn(value));
        }

        public Book build() {
            return book;
        }
    }
}
