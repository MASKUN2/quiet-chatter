package maskun.quietchatter.member.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MemberTest {

    @Test
    void newNaverMember() {
        String providerId = "naver123";
        String nickname = "NaverUser";
        Member member = Member.newNaverMember(providerId, nickname);

        assertThat(member.getStatus()).isEqualTo(Status.ACTIVE);
        assertThat(member.getRole()).isEqualTo(Role.REGULAR);
        assertThat(member.getNickname()).isEqualTo(nickname);
        assertThat(member.getProvider()).isEqualTo(OauthProvider.NAVER);
        assertThat(member.getProviderId()).isEqualTo(providerId);
    }
}
