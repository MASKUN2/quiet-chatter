package maskun.quietchatter.member.application.in;

import java.util.UUID;

public interface MemberCommandable {
    void updateNickname(UUID memberId, String nickname);
    void deactivate(UUID memberId);
    void activate(UUID memberId);
}
