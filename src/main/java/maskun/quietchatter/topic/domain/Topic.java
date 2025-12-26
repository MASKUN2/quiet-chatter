package maskun.quietchatter.topic.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.Getter;
import maskun.quietchatter.shared.persistence.AuditableUuidEntity;
import org.jspecify.annotations.Nullable;

@Getter
@Entity(name = "topic")
@Table(indexes = {@Index(columnList = "type", name = "idx_topic_type"),
        @Index(columnList = "parent_id", name = "idx_topic_parent_id")})
public class Topic extends AuditableUuidEntity {

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private Type type;

    @Column(name = "name")
    private String name;

    @Nullable
    @Column(name = "parent_id")
    private UUID parentId;

    public static Topic newOf(Type type, String name) {
        Topic topic = new Topic();
        topic.type = type;
        topic.name = name;
        return topic;
    }

    public static Topic newOf(Type type, String name, UUID parentId) {
        Topic topic = newOf(type, name);
        topic.parentId = parentId;
        return topic;
    }

    public boolean hasParent() {
        return parentId != null;
    }

    public enum Type {
        AUTHOR, WORK, BOOK
    }
}
