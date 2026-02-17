package maskun.quietchatter.security.application;

import maskun.quietchatter.member.application.in.MemberQueryable;
import maskun.quietchatter.member.application.in.MemberRegistrable;
import maskun.quietchatter.member.domain.Member;
import maskun.quietchatter.member.domain.Role;
import maskun.quietchatter.security.application.out.AuthMemberCache;
import maskun.quietchatter.security.domain.AuthMember;
import org.instancio.Select;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Instancio.of;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthMemberServiceTest {

    @Mock
    private AuthMemberCache authMemberCache;

    @Mock
    private MemberQueryable memberQueryable;

    @Mock
    private MemberRegistrable memberRegistrable;

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
}
