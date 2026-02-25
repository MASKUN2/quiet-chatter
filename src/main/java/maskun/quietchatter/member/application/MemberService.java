package maskun.quietchatter.member.application;

import java.util.Optional;
import java.util.UUID;

import org.jspecify.annotations.NullMarked;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import maskun.quietchatter.member.application.in.MemberQueryable;
import maskun.quietchatter.member.application.in.MemberRegistrable;
import maskun.quietchatter.member.application.out.MemberRepository;
import maskun.quietchatter.member.domain.Member;
import maskun.quietchatter.member.domain.OauthProvider;

@NullMarked
@Service
@RequiredArgsConstructor
class MemberService implements MemberRegistrable, MemberQueryable, maskun.quietchatter.member.application.in.MemberCommandable {
    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public Member createNewNaverMember(String providerId, String nickname) {
        Member member = Member.newNaverMember(providerId, nickname);
        return memberRepository.save(member);
    }

    @Override
    public Optional<Member> findById(UUID id) {
        return memberRepository.findById(id);
    }

    @Override
    public Optional<Member> findByNaverId(String providerId) {
        return memberRepository.findByProviderAndProviderId(OauthProvider.NAVER, providerId);
    }

    @Override
    @Transactional
    public void updateNickname(UUID memberId, String nickname) {
        Member member = memberRepository.findById(memberId).orElseThrow();
        member.updateNickname(nickname);
    }

    @Override
    @Transactional
    public void deactivate(UUID memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow();
        member.deactivate();
    }

    @Override
    @Transactional
    public void activate(UUID memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow();
        member.activate();
    }
}
