package maskun.quietchatter.member.application;

import lombok.RequiredArgsConstructor;
import maskun.quietchatter.member.application.in.MemberQueryable;
import maskun.quietchatter.member.application.in.MemberRegistrable;
import maskun.quietchatter.member.application.out.MemberRepository;
import maskun.quietchatter.member.application.out.RandomNickNameSupplier;
import maskun.quietchatter.member.domain.Member;
import maskun.quietchatter.member.domain.OauthProvider;
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
    @Transactional
    public Member createNewNaverMember(String providerId, String nickname) {
        String finalNickname = (nickname == null || nickname.isBlank()) ? randomNickNameSupplier.get() : nickname;
        Member member = Member.newNaverMember(providerId, finalNickname);
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
}
