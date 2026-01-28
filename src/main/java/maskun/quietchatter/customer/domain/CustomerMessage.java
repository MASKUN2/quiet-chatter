package maskun.quietchatter.customer.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import maskun.quietchatter.shared.persistence.BaseEntity;

@Getter
@Entity(name = "customer_message")
public class CustomerMessage extends BaseEntity {

    @Column(name = "message", columnDefinition = "TEXT")
    private String message;

    public void update(String message) {
        this.message = message;
    }
}
