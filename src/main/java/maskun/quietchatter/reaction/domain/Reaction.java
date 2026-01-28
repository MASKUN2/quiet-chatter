package maskun.quietchatter.reaction.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import maskun.quietchatter.shared.persistence.BaseEntity;

import java.util.Objects;
import java.util.UUID;

@Getter
@Entity(name = "reaction")
@Table(indexes = {@Index(columnList = "talk_id", name = "idx_reaction_talk_id"),
        @Index(columnList = "member_id", name = "idx_reaction_member_id"),
        @Index(columnList = "type", name = "idx_reaction_type")},
        uniqueConstraints = @UniqueConstraint(columnNames = {"talk_id", "member_id", "type"},
                name = "uq_reaction_talk_member_type"))
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Reaction extends BaseEntity {

    @Column(name = "talk_id")
    private UUID talkId;

    @Column(name = "member_id")
    private UUID memberId;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private Type type;

    public Reaction(UUID talkId, UUID memberId, Type type) {
        this.talkId = Objects.requireNonNull(talkId, "talkId must not be null");
        this.memberId = Objects.requireNonNull(memberId, "memberId must not be null");
        this.type = Objects.requireNonNull(type, "type must not be null");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Reaction reaction)) {
            return false;
        }
        return Objects.equals(getTalkId(), reaction.getTalkId()) &&
                Objects.equals(getMemberId(), reaction.getMemberId()) &&
                getType() == reaction.getType();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getTalkId(), getMemberId(), getType());
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" +
                "id = " + id + ", " +
                "talkId = " + talkId + ", " +
                "memberId = " + memberId + ", " +
                "type = " + type + ")";
    }

    public enum Type {
        LIKE,
        SUPPORT
    }
}
