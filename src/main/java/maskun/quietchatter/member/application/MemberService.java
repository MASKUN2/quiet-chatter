package maskun.quietchatter.member.application;

import lombok.RequiredArgsConstructor;
import maskun.quietchatter.member.application.in.MemberQueryable;
import maskun.quietchatter.member.application.in.MemberRegistrable;
import maskun.quietchatter.member.application.out.MemberRepository;
import maskun.quietchatter.member.application.out.RandomNickNameSupplier;
import maskun.quietchatter.member.domain.Member;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
class MemberService implements MemberRegistrable, MemberQueryable {
    private final MemberRepository memberRepository;
    private final RandomNickNameSupplier randomNickNameSupplier;

    @Override
    @Transactional
    public Member createNewGuest() {
        Member member = Member.newGuest(randomNickNameSupplier.get());
        return memberRepository.save(member);
    }

    @Override
    public Optional<Member> findById(UUID id) {
        return memberRepository.findById(id);
    }
}
