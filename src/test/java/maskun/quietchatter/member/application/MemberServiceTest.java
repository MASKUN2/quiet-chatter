package maskun.quietchatter.member.application;

import maskun.quietchatter.member.application.out.MemberRepository;
import maskun.quietchatter.member.application.out.RandomNickNameSupplier;
import maskun.quietchatter.member.domain.Member;
import maskun.quietchatter.member.domain.OauthProvider;
import maskun.quietchatter.member.domain.Role;
import maskun.quietchatter.member.domain.Status;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private RandomNickNameSupplier randomNickNameSupplier;

    @InjectMocks
    private MemberService memberService;

    @Test
    @DisplayName("네이버 회원을 생성한다")
    void createNewNaverMember() {
        // given
        String providerId = "naver123";
        String nickname = "NaverUser";
        given(memberRepository.save(any(Member.class))).willAnswer(invocation -> invocation.getArgument(0));

        // when
        Member newMember = memberService.createNewNaverMember(providerId, nickname);

        // then
        assertThat(newMember.getNickname()).isEqualTo(nickname);
        assertThat(newMember.getRole()).isEqualTo(Role.REGULAR);
        assertThat(newMember.getStatus()).isEqualTo(Status.ACTIVE);
        assertThat(newMember.getProvider()).isEqualTo(OauthProvider.NAVER);
        assertThat(newMember.getProviderId()).isEqualTo(providerId);

        verify(memberRepository).save(any(Member.class));
    }

    @Test
    @DisplayName("네이버 회원 생성 시 닉네임이 없으면 랜덤 닉네임을 사용한다")
    void createNewNaverMemberWithRandomNickname() {
        // given
        String providerId = "naver123";
        String expectedNickname = "RandomNickname123";
        given(randomNickNameSupplier.get()).willReturn(expectedNickname);
        given(memberRepository.save(any(Member.class))).willAnswer(invocation -> invocation.getArgument(0));

        // when
        Member newMember = memberService.createNewNaverMember(providerId, null);

        // then
        assertThat(newMember.getNickname()).isEqualTo(expectedNickname);
        assertThat(newMember.getRole()).isEqualTo(Role.REGULAR);

        verify(randomNickNameSupplier).get();
        verify(memberRepository).save(any(Member.class));
    }
}
