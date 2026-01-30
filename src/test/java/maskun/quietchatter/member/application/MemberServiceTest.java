package maskun.quietchatter.member.application;

import maskun.quietchatter.member.application.out.MemberRepository;
import maskun.quietchatter.member.application.out.RandomNickNameSupplier;
import maskun.quietchatter.member.domain.Member;
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
    @DisplayName("게스트 회원을 생성할 때 랜덤 닉네임을 사용한다")
    void createNewGuest() {
        // given
        String expectedNickname = "RandomNickname123";
        given(randomNickNameSupplier.get()).willReturn(expectedNickname);
        given(memberRepository.save(any(Member.class))).willAnswer(invocation -> invocation.getArgument(0));

        // when
        Member newGuest = memberService.createNewGuest();

        // then
        assertThat(newGuest.getNickname()).isEqualTo(expectedNickname);
        assertThat(newGuest.getRole()).isEqualTo(Role.GUEST);
        assertThat(newGuest.getStatus()).isEqualTo(Status.ACTIVE);

        verify(randomNickNameSupplier).get();
        verify(memberRepository).save(any(Member.class));
    }
}
