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

    @Column(name = "provider")
    @Enumerated(EnumType.STRING)
    private OauthProvider provider;

    @Column(name = "provider_id")
    private String providerId;

    public static Member newNaverMember(String providerId, String nickname) {
        Member member = new Member();
        member.nickname = nickname;
        member.role = Role.REGULAR;
        member.status = Status.ACTIVE;
        member.provider = OauthProvider.NAVER;
        member.providerId = providerId;
        return member;
    }
}
