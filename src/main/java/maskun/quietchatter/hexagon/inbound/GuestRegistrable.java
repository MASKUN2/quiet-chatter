package maskun.quietchatter.hexagon.inbound;

import maskun.quietchatter.hexagon.domain.member.Member;

public interface GuestRegistrable {
    Member createNewGuest();
}
