package maskun.quietchatter.hexagon.application;

import lombok.RequiredArgsConstructor;
import maskun.quietchatter.hexagon.domain.member.Member;
import maskun.quietchatter.hexagon.inbound.GuestRegistrable;
import maskun.quietchatter.hexagon.outbound.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberRegistrator implements GuestRegistrable {
    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public Member createNewGuest() {
        Member member = Member.newGuest();
        return memberRepository.save(member);
    }
}
