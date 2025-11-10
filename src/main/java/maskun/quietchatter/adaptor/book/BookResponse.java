package maskun.quietchatter.adaptor.book;

import java.io.Serializable;
import java.util.UUID;
import maskun.quietchatter.hexagon.domain.book.Book;

/**
 * DTO for {@link Book}
 */
public record BookResponse(
        UUID id,
        String title,
        String isbn) implements Serializable {

    public static BookResponse from(Book book) {
        return new BookResponse(
                book.getId(),
                book.getTitle().value(),
                book.getIsbn().value()
        );
    }
}
