package maskun.quietchatter.hexagon.domain.book;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import maskun.quietchatter.hexagon.domain.BaseEntity;

@Entity(name = "Book")
public class Book extends BaseEntity {

    @Embedded
    private Title title;

    @Embedded
    private Isbn isbn;

    public static Book newOf(Title title, Isbn isbn) {
        Book book = new Book();
        book.title = title;
        book.isbn = isbn;
        return book;
    }

    public Title getTitle() {
        return title;
    }

    public Isbn getIsbn() {
        return isbn;
    }

    public void update(Title title) {
        this.title = title;
    }

    public void update(Isbn isbn) {
        this.isbn = isbn;
    }
}
