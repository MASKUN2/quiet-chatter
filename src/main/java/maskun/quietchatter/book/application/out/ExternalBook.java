package maskun.quietchatter.book.application.out;

public record ExternalBook(
        String title,
        String isbn,
        String author,
        String thumbnailImage,
        String description,
        String externalLink
) {
}
