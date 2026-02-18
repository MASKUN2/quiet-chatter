package maskun.quietchatter.security.application;

import maskun.quietchatter.member.application.in.MemberQueryable;
import maskun.quietchatter.member.application.in.MemberRegistrable;
import maskun.quietchatter.member.domain.Member;
import maskun.quietchatter.member.domain.Role;
import maskun.quietchatter.security.adaptor.out.NaverClient;
import maskun.quietchatter.security.adaptor.out.NaverProfileResponse;
import maskun.quietchatter.security.adaptor.out.NaverTokenResponse;
import maskun.quietchatter.security.application.in.AuthMemberNotFoundException;
import maskun.quietchatter.security.application.in.AuthMemberService.NaverProfile;
import maskun.quietchatter.security.application.out.AuthMemberCache;
import maskun.quietchatter.security.domain.AuthMember;
import org.instancio.Select;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.instancio.Instancio.of;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthMemberServiceTest {

    @Mock
    private AuthMemberCache authMemberCache;

    @Mock
    private MemberQueryable memberQueryable;

    @Mock
    private MemberRegistrable memberRegistrable;

    @Mock
    private NaverClient naverClient;

    @InjectMocks
    private AuthMemberServiceImpl authMemberService;

    @Test
    void loadAuthMemberFromMainDB() {
        UUID myId = UUID.randomUUID();
        Member member = of(Member.class)
                .set(Select.field(Member::getId), myId)
                .set(Select.field(Member::getRole), Role.REGULAR)
                .create();

        when(authMemberCache.findById(myId)).thenReturn(Optional.empty());
        when(memberQueryable.findById(eq(myId))).thenReturn(Optional.ofNullable(member));
        
        Optional<AuthMember> found = authMemberService.findById(myId);

        assertThat(found).isPresent();
        verify(authMemberCache).save(any(AuthMember.class));
    }

    @Test
    void cacheHit() {
        UUID myId = UUID.randomUUID();
        AuthMember authMember = new AuthMember(myId, Role.REGULAR);

        when(authMemberCache.findById(myId)).thenReturn(Optional.of(authMember));

        AuthMember found = authMemberService.findById(myId).orElseThrow();

        assertThat(found).isEqualTo(authMember);
        verify(memberQueryable, times(0)).findById(any()); 
    }

    @Test
    @DisplayName("네이버 로그인 시 프로필 정보를 반환한다")
    void loginWithNaver() {
        String code = "code";
        String state = "state";
        String accessToken = "access";
        String providerId = "naver123";
        String nickname = "nickname";

        NaverTokenResponse tokenResponse = new NaverTokenResponse(accessToken, null, null, null, null, null);
        NaverProfileResponse.Response profileData = new NaverProfileResponse.Response(providerId, nickname, null, null, null, null, null, null, null, null);
        NaverProfileResponse profileResponse = new NaverProfileResponse("00", "success", profileData);

        given(naverClient.getAccessToken(code, state)).willReturn(tokenResponse);
        given(naverClient.getProfile(accessToken)).willReturn(profileResponse);

        NaverProfile result = authMemberService.loginWithNaver(code, state);

        assertThat(result.providerId()).isEqualTo(providerId);
        assertThat(result.nickname()).isEqualTo(nickname);
    }

    @Test
    @DisplayName("네이버 ID로 회원을 조회한다")
    void getByNaverId() {
        String providerId = "naver123";
        Member member = of(Member.class)
                .set(Select.field(Member::getId), UUID.randomUUID())
                .set(Select.field(Member::getRole), Role.REGULAR)
                .create();

        given(memberQueryable.findByNaverId(providerId)).willReturn(Optional.of(member));

        AuthMember authMember = authMemberService.getByNaverId(providerId);

        assertThat(authMember).isNotNull();
        verify(authMemberCache).save(any(AuthMember.class));
    }

    @Test
    @DisplayName("네이버 ID로 회원을 조회 실패 시 예외 발생")
    void getByNaverId_NotFound() {
        String providerId = "naver123";
        given(memberQueryable.findByNaverId(providerId)).willReturn(Optional.empty());

        assertThatThrownBy(() -> authMemberService.getByNaverId(providerId))
                .isInstanceOf(AuthMemberNotFoundException.class);
    }

    @Test
    @DisplayName("네이버 회원가입을 처리한다")
    void signupWithNaver() {
        String providerId = "naver123";
        String nickname = "newNick";
        Member member = of(Member.class)
                .set(Select.field(Member::getId), UUID.randomUUID())
                .set(Select.field(Member::getRole), Role.REGULAR)
                .create();

        given(memberQueryable.findByNaverId(providerId)).willReturn(Optional.empty());
        given(memberRegistrable.createNewNaverMember(providerId, nickname)).willReturn(member);

        AuthMember authMember = authMemberService.signupWithNaver(providerId, nickname);

        assertThat(authMember).isNotNull();
        verify(authMemberCache).save(any(AuthMember.class));
    }
}
