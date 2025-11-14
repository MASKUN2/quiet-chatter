package maskun.quietchatter.hexagon.domain.talk;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.Getter;
import maskun.quietchatter.hexagon.domain.BaseEntity;
import maskun.quietchatter.hexagon.domain.book.Book;
import maskun.quietchatter.hexagon.domain.member.Member;

@Getter
@Entity(name = "talk")
@Table(indexes = @Index(columnList = "book_id", name = "idx_talk_book_id"))
public class Talk extends BaseEntity {

    @ManyToOne(fetch = jakarta.persistence.FetchType.LAZY)
    @JoinColumn(name = "book_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Book book;

    @ManyToOne(fetch = jakarta.persistence.FetchType.LAZY)
    @JoinColumn(name = "author_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Member author;

    @Embedded
    private Content content;

    @Embedded
    @AttributeOverride(name = "hidden", column = @Column(name = "time_to_hidden"))
    private Time time;

    public static Talk newOf(Book book, Member author, Content content, Time time) {
        Talk talk = new Talk();
        talk.book = book;
        talk.author = author;
        talk.content = content;
        talk.time = time;
        return talk;
    }

    public void update(Book book) {
        this.book = book;
    }

    public void update(Content content) {
        this.content = content;
    }

    public boolean isHidden(Instant reference) {
        Instant hidden = time.hidden();
        return reference.isAfter(hidden);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" +
                "id = " + getId() + ", " +
                "book = " + getBook() + ", " +
                "content = " + getContent() + ", " +
                "createdAt = " + getCreatedAt() + ")";
    }
}
