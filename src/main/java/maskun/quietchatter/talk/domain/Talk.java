package maskun.quietchatter.talk.domain;

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
import lombok.NoArgsConstructor;
import maskun.quietchatter.shared.persistence.BaseEntity;

@Getter
@Entity(name = "talk")
@Table(indexes = {
        @Index(columnList = "book_id", name = "idx_talk_book_id"),
        @Index(columnList = "member_id", name = "idx_talk_member_id"),
        @Index(columnList = "created_at", name = "idx_talk_created_at"),
        @Index(columnList = "date_to_hidden, is_hidden", name = "idx_talk_date_to_hidden_is_hidden")
})
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
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

    @Column(name = "like_count", updatable = false)
    private long likeCount;

    @Column(name = "support_count", updatable = false)
    private long supportCount;

    public Talk(UUID bookId, UUID memberId, Content content) {
        this(bookId, memberId, content, LocalDate.now().plusMonths(12));
    }

    public Talk(UUID bookId, UUID memberId, Content content, LocalDate dateToHidden) {
        this.bookId = bookId;
        this.memberId = memberId;
        this.content = content;
        this.dateToHidden = dateToHidden;
        this.isHidden = false;
        this.likeCount = 0;
        this.supportCount = 0;
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
                "likeCount = " + getLikeCount() + ", " +
                "supportCount = " + getSupportCount() + ", " +
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
