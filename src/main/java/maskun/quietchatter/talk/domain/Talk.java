package maskun.quietchatter.talk.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import maskun.quietchatter.persistence.BaseEntity;
import org.hibernate.annotations.Comment;

import java.time.LocalDate;
import java.util.UUID;

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

    public static final int MAX_CONTENT_LENGTH = 250;
    public static final int DEFAULT_HIDDEN_MONTHS = 12;

    @Column(name = "book_id")
    private UUID bookId;

    @Comment("작성 당시의 닉네임을 둔다(중복허용)")
    @Column(name = "nickname")
    private String nickname;

    @Column(name = "member_id")
    private UUID memberId;

    @Column(name = "content", length = MAX_CONTENT_LENGTH)
    private String content;

    @Column(name = "date_to_hidden")
    private LocalDate dateToHidden;

    @Column(name = "is_hidden")
    private boolean isHidden;

    @Column(name = "like_count", updatable = false)
    private long likeCount;

    @Column(name = "support_count", updatable = false)
    private long supportCount;

    public Talk(UUID bookId, UUID memberId, String nickname, String content) {
        this(bookId, memberId, nickname, content, LocalDate.now().plusMonths(DEFAULT_HIDDEN_MONTHS)); // default
    }

    public Talk(UUID bookId, UUID memberId, String nickname, String content, LocalDate dateToHidden) {
        validateContent(content);
        this.bookId = bookId;
        this.memberId = memberId;
        this.nickname = nickname;
        this.content = content;
        this.dateToHidden = dateToHidden;
        this.isHidden = false;
        this.likeCount = 0;
        this.supportCount = 0;
    }

    private void validateContent(String content) {
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("내용은 비어있을 수 없습니다.");
        }
        if (content.length() > MAX_CONTENT_LENGTH) {
            throw new IllegalArgumentException("내용은 %d 자를 초과할 수 없습니다.".formatted(MAX_CONTENT_LENGTH));
        }
    }

    public void updateContent(String content) {
        validateContent(content);
        this.content = content;
        this.dateToHidden = LocalDate.now().plusMonths(DEFAULT_HIDDEN_MONTHS);
    }

    public void hide() {
        this.isHidden = true;
    }

    public boolean isModified() {
        if (createdAt == null || this.lastModifiedAt == null) {
            return false;
        }
        return lastModifiedAt.isAfter(createdAt);
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
}
