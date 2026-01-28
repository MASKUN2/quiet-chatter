package maskun.quietchatter.member.domain;

import jakarta.persistence.*;
import lombok.Getter;
import maskun.quietchatter.shared.persistence.BaseEntity;

@Getter
@Entity(name = "member")
@Table(indexes = @Index(columnList = "login_id", name = "idx_member_login_id"))
public class Member extends BaseEntity {

    @Enumerated(EnumType.STRING)
    private Role role;

    @Enumerated(EnumType.STRING)
    private Status status;

    public static Member newGuest() {
        Member member = new Member();
        member.role = Role.GUEST;
        member.status = Status.ACTIVE;
        return member;
    }
}
