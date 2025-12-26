package maskun.quietchatter.book.domain;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Getter;
import maskun.quietchatter.shared.persistence.AuditableUuidEntity;

@Getter
@Entity(name = "book")
@Table(indexes = {@Index(columnList = "isbn", name = "idx_book_isbn")
        , @Index(columnList = "title", name = "idx_book_title")})
public class Book extends AuditableUuidEntity {

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "title"))
    private Title title;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "isbn"))
    private Isbn isbn;

    @Embedded
    @AttributeOverride(name = "name", column = @Column(name = "author"))
    private Author author;

    @Embedded
    @AttributeOverride(name = "url", column = @Column(name = "thumbnail_image_url", columnDefinition = "TEXT"))
    private ThumbnailImage thumbnailImage;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "description", columnDefinition = "TEXT"))
    private Description description;

    @Embedded
    @AttributeOverride(name = "url", column = @Column(name = "external_link_url", columnDefinition = "TEXT"))
    private ExternalLink externalLink;

    public static Book newOf(Title title, Isbn isbn) {
        Book book = new Book();
        book.title = title;
        book.isbn = isbn;
        return book;
    }

    public void update(Title title) {
        this.title = title;
    }

    public void update(Author author) {
        this.author = author;
    }

    public void update(ThumbnailImage thumbnailImage) {
        this.thumbnailImage = thumbnailImage;
    }

    public void update(Description description) {
        this.description = description;
    }

    public void update(ExternalLink externalLink) {
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
