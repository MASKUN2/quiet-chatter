package maskun.quietchatter.member.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.NoArgsConstructor;
import maskun.quietchatter.persistence.BaseEntity;

@Getter
@Entity(name = "member")
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class Member extends BaseEntity {

    @Column(name = "nickname")
    private String nickname;

    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private Status status;

    public static Member newGuest() {
        Member member = new Member();
        member.nickname = "guest";
        member.role = Role.GUEST;
        member.status = Status.ACTIVE;
        return member;
    }

    public static Member newGuest(String nickname) {
        Member member = newGuest();
        member.nickname = nickname;
        return member;
    }
}
