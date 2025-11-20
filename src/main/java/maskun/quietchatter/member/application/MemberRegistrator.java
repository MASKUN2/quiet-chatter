package maskun.quietchatter.member.application;

import lombok.RequiredArgsConstructor;
import maskun.quietchatter.member.application.in.GuestRegistrable;
import maskun.quietchatter.member.application.out.MemberRepository;
import maskun.quietchatter.member.domain.Member;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
class MemberRegistrator implements GuestRegistrable {
    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public Member createNewGuest() {
        Member member = Member.newGuest();
        return memberRepository.save(member);
    }
}
