package maskun.quietchatter.member.application.in;

import maskun.quietchatter.member.domain.Member;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface MemberRegistrable {
    Member createNewNaverMember(String providerId, String nickname);
}
