package maskun.quietchatter.member.domain;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Getter;
import maskun.quietchatter.shared.persistence.AuditableUuidEntity;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

@Getter
@Entity(name = "member")
@Table(indexes = @Index(columnList = "login_id", name = "idx_member_login_id"))
public class Member extends AuditableUuidEntity {

    @Enumerated(EnumType.STRING)
    private Role role;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Embedded
    @AttributeOverrides({@AttributeOverride(name = "id.value", column = @Column(name = "login_id")),
            @AttributeOverride(name = "password.hash", column = @Column(name = "login_password",
                    columnDefinition = "TEXT"))})
    private Login login;

    public static Member newGuest() {
        Member member = new Member();
        member.role = Role.GUEST;
        member.status = Status.ACTIVE;
        return member;
    }

    /**
     * 게스트를 정회원으로 승격
     */
    public void promote(Login login) {
        Assert.isTrue(status.equals(Status.ACTIVE), "회원이 활성상태가 아닙니다");
        Assert.isTrue(role.equals(Role.GUEST), "회원이 게스트가 아닙니다");
        this.login = login;
        this.role = Role.REGULAR;
    }

    @Nullable
    public Login getLogin() {
        return login;
    }
}
