package maskun.quietchatter.book.adaptor.in;

import java.io.Serializable;
import java.util.UUID;
import maskun.quietchatter.book.domain.Book;

/**
 * DTO for {@link Book}
 */
record BookResponse(
        UUID id,
        String title,
        String isbn,
        String author,
        String thumbnailImageUrl,
        String description,
        String externalLinkUrl) implements Serializable {

    static BookResponse from(Book book) {
        return new BookResponse(
                book.getId(),
                book.getTitle(),
                book.getIsbn(),
                book.getAuthor(),
                book.getThumbnailImage(),
                book.getDescription(),
                book.getExternalLink()
        );
    }
}
