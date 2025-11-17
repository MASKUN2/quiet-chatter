package maskun.quietchatter.hexagon.domain.talk;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import maskun.quietchatter.hexagon.domain.BaseEntity;

@Getter
@Entity(name = "talk")
@Table(indexes = {@Index(columnList = "book_id", name = "idx_talk_book_id"),
        @Index(columnList = "member_id", name = "idx_talk_member_id")})
public class Talk extends BaseEntity {

    @Column(name = "book_id")
    private UUID bookId;

    @Column(name = "member_id")
    private UUID memberId;

    @Embedded
    private Content content;

    @Embedded
    @AttributeOverride(name = "hidden", column = @Column(name = "time_to_hidden"))
    private Time time;

    @Embedded
    @AttributeOverrides(
            value = {@AttributeOverride(name = "like", column = @Column(name = "like_count", updatable = false)),
                    @AttributeOverride(name = "support", column = @Column(name = "support_count", updatable = false))}
    )
    private ReactionCount reactionCount;

    public Talk() {
        reactionCount = ReactionCount.zero();
        time = new Time(null);
    }

    public void updateBookId(UUID bookId) {
        this.bookId = bookId;
    }

    public void updateMemberId(UUID memberId) {
        this.memberId = memberId;
    }

    public void update(Content content) {
        this.content = content;
    }

    public void update(Time time) {
        this.time = time;
    }

    public boolean isHidden(Instant reference) {
        Instant hidden = time.hidden();
        return reference.isAfter(hidden);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" +
                "id = " + getId() + ", " +
                "bookId = " + getBookId() + ", " +
                "memberId = " + getMemberId() + ", " +
                "content = " + getContent() + ", " +
                "time = " + getTime() + ", " +
                "createdAt = " + getCreatedAt() + ")";
    }
}
