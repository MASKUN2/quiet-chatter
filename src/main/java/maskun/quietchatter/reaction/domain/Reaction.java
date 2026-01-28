package maskun.quietchatter.reaction.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import maskun.quietchatter.shared.persistence.BaseEntity;

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
        this.talkId = talkId;
        this.memberId = memberId;
        this.type = type;
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
