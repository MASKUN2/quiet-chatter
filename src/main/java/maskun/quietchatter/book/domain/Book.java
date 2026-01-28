package maskun.quietchatter.book.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Getter;
import maskun.quietchatter.shared.persistence.BaseEntity;

@Getter
@Entity(name = "book")
@Table(indexes = {@Index(columnList = "isbn", name = "idx_book_isbn")
        , @Index(columnList = "title", name = "idx_book_title")})
public class Book extends BaseEntity {

    @Column(name = "title")
    private String title;

    @Column(name = "isbn")
    private String isbn;

    @Column(name = "author")
    private String author;

    @Column(name = "thumbnail_image_url", columnDefinition = "TEXT")
    private String thumbnailImage;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "external_link_url", columnDefinition = "TEXT")
    private String externalLink;

    public static Book newOf(String title, String isbn) {
        Book book = new Book();
        book.title = title;
        book.isbn = isbn;
        return book;
    }

    public void update(String title) {
        this.title = title;
    }

    public void updateAuthor(String author) {
        this.author = author;
    }

    public void updateThumbnailImage(String thumbnailImage) {
        this.thumbnailImage = thumbnailImage;
    }

    public void updateDescription(String description) {
        this.description = description;
    }

    public void updateExternalLink(String externalLink) {
        this.externalLink = externalLink;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" +
                "id = " + getId() + ", " +
                "externalLink = " + getExternalLink() + ", " +
                "title = " + getTitle() + ", " +
                "isbn = " + getIsbn() + ", " +
                "author = " + getAuthor() + ", " +
                "thumbnailImage = " + getThumbnailImage() + ", " +
                "description = " + getDescription() + ", " +
                "createdAt = " + getCreatedAt() + ")";
    }
}
