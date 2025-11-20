package maskun.quietchatter.member.application.in;

import maskun.quietchatter.member.domain.Member;

public interface GuestRegistrable {
    Member createNewGuest();
}
