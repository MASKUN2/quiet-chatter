package maskun.quietchatter.hexagon.domain.book;

import static org.instancio.Instancio.of;
import static org.instancio.Select.field;
import static org.instancio.Select.fields;

import java.util.Locale;
import maskun.quietchatter.hexagon.domain.BaseEntity;
import net.datafaker.Faker;
import org.instancio.InstancioApi;

public class BookFixture {

    public InstancioApi<Book> asNew() {
        return initBook().ignore(fields().declaredIn(BaseEntity.class));
    }

    private static InstancioApi<Book> initBook() {
        final Faker faker = new Faker(Locale.KOREA);

        return of(Book.class)
                .set(field(Book::getTitle), new Title(faker.book().title()))
                .set(field(Book::getIsbn), new Isbn(faker.code().isbn13()));
    }

    public InstancioApi<Book> asPersisted() {
        return initBook();
    }
}
