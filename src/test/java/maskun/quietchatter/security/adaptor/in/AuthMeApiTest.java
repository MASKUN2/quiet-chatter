package maskun.quietchatter.security.adaptor.in;

import maskun.quietchatter.MockSecurityTestConfig;
import maskun.quietchatter.member.application.in.MemberQueryable;
import maskun.quietchatter.member.domain.Member;
import maskun.quietchatter.member.domain.Role;
import maskun.quietchatter.security.adaptor.AuthMemberToken;
import maskun.quietchatter.security.domain.AuthMember;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.anonymous;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;

@WebMvcTest(controllers = AuthMeApi.class)
@Import(MockSecurityTestConfig.class)
class AuthMeApiTest {

    @Autowired
    private MockMvcTester tester;

    @MockitoBean
    private MemberQueryable memberQueryable;

    @Test
    @DisplayName("인증된 사용자 정보 조회")
    void me_Authenticated() {
        UUID memberId = UUID.randomUUID();
        AuthMember authMember = new AuthMember(memberId, Role.REGULAR);
        Member member = Member.newGuest("testUser");

        given(memberQueryable.findById(any(UUID.class))).willReturn(Optional.of(member));

        tester.get().uri("/api/v1/auth/me")
                .with(authentication(new AuthMemberToken(authMember)))
                .exchange()
                .assertThat()
                .hasStatusOk()
                .bodyJson()
                .hasPathSatisfying("$.nickname", v -> assertThat(v).isEqualTo("testUser"))
                .hasPathSatisfying("$.isLoggedIn", v -> assertThat(v).isEqualTo(true))
                .hasPathSatisfying("$.id", v -> assertThat(v).isEqualTo(memberId.toString()))
                .hasPathSatisfying("$.role", v -> assertThat(v).isEqualTo("REGULAR"));
    }

    @Test
    @DisplayName("인증되지 않은 사용자 정보 조회")
    void me_Unauthenticated() {
        tester.get().uri("/api/v1/auth/me")
                .with(anonymous())
                .exchange()
                .assertThat()
                .hasStatusOk()
                .bodyJson()
                .hasPathSatisfying("$.nickname", v -> assertThat(v).isEqualTo("anonymous"))
                .hasPathSatisfying("$.isLoggedIn", v -> assertThat(v).isEqualTo(false))
                .hasPathSatisfying("$.id", v -> assertThat(v).isNull())
                .hasPathSatisfying("$.role", v -> assertThat(v).isEqualTo("anonymous"));
    }
}
