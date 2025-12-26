package maskun.quietchatter.shared.persistence;

import jakarta.persistence.EntityListeners;
import java.time.Instant;
import java.util.StringJoiner;
import org.jspecify.annotations.Nullable;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@EntityListeners(AuditingEntityListener.class)
public abstract class AuditableUuidEntity extends UuidEntity {

    @CreatedDate
    private Instant createdAt;

    @Nullable
    public Instant getCreatedAt() {
        return createdAt;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", AuditableUuidEntity.class.getSimpleName() + "[", "]")
                .add("createdAt=" + createdAt)
                .add("id=" + getId())
                .toString();
    }
}

