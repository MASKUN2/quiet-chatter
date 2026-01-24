package maskun.quietchatter.talk.domain;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.UUID;
import lombok.Getter;
import maskun.quietchatter.shared.persistence.BaseEntity;

@Getter
@Entity(name = "talk")
@Table(indexes = {
        @Index(columnList = "book_id", name = "idx_talk_book_id"),
        @Index(columnList = "member_id", name = "idx_talk_member_id"),
        @Index(columnList = "created_at", name = "idx_talk_created_at"),
        @Index(columnList = "date_to_hidden, is_hidden", name = "idx_talk_date_to_hidden_is_hidden")
})
public class Talk extends BaseEntity {

    @Column(name = "book_id")
    private UUID bookId;

    @Column(name = "member_id")
    private UUID memberId;

    @Embedded
    private Content content;

    @Column(name = "date_to_hidden")
    private LocalDate dateToHidden;

    @Column(name = "is_hidden")
    private boolean isHidden;

    @Embedded
    @AttributeOverrides(
            value = {@AttributeOverride(name = "like", column = @Column(name = "like_count", updatable = false)),
                    @AttributeOverride(name = "support", column = @Column(name = "support_count", updatable = false))}
    )
    private ReactionCount reactionCount;

    protected Talk() {
        // JPA용
    }

    public Talk(UUID bookId, UUID memberId, Content content) {
        this(bookId, memberId, content, LocalDate.now().plusMonths(12));
    }

    public Talk(UUID bookId, UUID memberId, Content content, LocalDate dateToHidden) {
        this.bookId = bookId;
        this.memberId = memberId;
        this.content = content;
        this.dateToHidden = dateToHidden;
        this.isHidden = false;
        this.reactionCount = ReactionCount.zero();
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

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" +
                "id = " + getId() + ", " +
                "bookId = " + getBookId() + ", " +
                "memberId = " + getMemberId() + ", " +
                "content = " + getContent() + ", " +
                "dateToHidden = " + getDateToHidden() + ", " +
                "isHidden = " + isHidden() + ", " +
                "createdAt = " + getCreatedAt() + ")";
    }

    // 호환성을 위해 임시로 추가 (Time 객체 제거 후 수정 필요)
    public Instant getHiddenTimeAsInstant() {
        if (dateToHidden == null) {
            return null;
        }
        return dateToHidden.atStartOfDay(ZoneId.systemDefault()).toInstant();
    }
}
