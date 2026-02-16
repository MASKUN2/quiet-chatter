package maskun.quietchatter.member.application.in;

import maskun.quietchatter.member.domain.Member;

public interface MemberRegistrable {
    Member createNewGuest();
    Member createNewNaverMember(String providerId, String nickname);
}
