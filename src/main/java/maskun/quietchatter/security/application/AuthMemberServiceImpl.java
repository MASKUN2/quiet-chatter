package maskun.quietchatter.security.application;

import maskun.quietchatter.member.application.in.MemberQueryable;
import maskun.quietchatter.member.application.in.MemberRegistrable;
import maskun.quietchatter.member.domain.Member;
import maskun.quietchatter.security.adaptor.out.NaverClient;
import maskun.quietchatter.security.adaptor.out.NaverProfileResponse;
import maskun.quietchatter.security.adaptor.out.NaverTokenResponse;
import maskun.quietchatter.security.application.in.AuthMemberNotFoundException;
import maskun.quietchatter.security.application.in.AuthMemberService;
import maskun.quietchatter.security.application.out.AuthMemberCache;
import maskun.quietchatter.security.domain.AuthMember;
import org.jspecify.annotations.NullMarked;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@NullMarked
@Service
class AuthMemberServiceImpl implements AuthMemberService {
    private final AuthMemberCache authMemberCache;
    private final MemberQueryable memberQueryable;
    private final MemberRegistrable memberRegistrable;
    private final NaverClient naverClient;

    AuthMemberServiceImpl(
            AuthMemberCache authMemberCache,
            MemberQueryable memberQueryable,
            MemberRegistrable memberRegistrable,
            NaverClient naverClient) {
        this.authMemberCache = authMemberCache;
        this.memberQueryable = memberQueryable;
        this.memberRegistrable = memberRegistrable;
        this.naverClient = naverClient;
    }

    @Override
    public Optional<AuthMember> findById(UUID id) {
        Optional<AuthMember> cachedAuthMember = authMemberCache.findById(id);
        if (cachedAuthMember.isPresent()) {
            return cachedAuthMember;
        }

        Optional<Member> foundMember = memberQueryable.findById(id);
        if (foundMember.isEmpty()) {
            return Optional.empty();
        }

        AuthMember authMember = getAuthMember(foundMember.get());
        authMemberCache.save(authMember);
        return Optional.of(authMember);
    }

    @Override
    public AuthMember findOrThrow(UUID id) throws AuthMemberNotFoundException {
        return findById(id).orElseThrow(() -> new AuthMemberNotFoundException("member not found for id: " + id));
    }

    @Override
    @Transactional
    public NaverProfile loginWithNaver(String code, String state) {
        NaverTokenResponse tokenResponse = naverClient.getAccessToken(code, state);
        if (tokenResponse.accessToken() == null) {
            throw new RuntimeException("Failed to get Naver access token: " + tokenResponse.errorDescription());
        }

        NaverProfileResponse profileResponse = naverClient.getProfile(tokenResponse.accessToken());
        if (profileResponse.response() == null) {
            throw new RuntimeException("Failed to get Naver profile: " + profileResponse.message());
        }

        String providerId = profileResponse.response().id();
        String nickname = profileResponse.response().nickname();

        return new NaverProfile(providerId, nickname);
    }

    @Override
    public AuthMember getByNaverId(String providerId) throws AuthMemberNotFoundException {
        Member member = memberQueryable.findByNaverId(providerId)
                .orElseThrow(() -> new AuthMemberNotFoundException("member not found for naver id: " + providerId));
        AuthMember authMember = getAuthMember(member);
        authMemberCache.save(authMember);
        return authMember;
    }

    @Override
    @Transactional
    public AuthMember signupWithNaver(String providerId, String nickname) {
        if (memberQueryable.findByNaverId(providerId).isPresent()) {
            throw new IllegalArgumentException("이미 가입된 회원입니다.");
        }

        Member member = memberRegistrable.createNewNaverMember(providerId, nickname);
        AuthMember authMember = getAuthMember(member);
        authMemberCache.save(authMember);
        return authMember;
    }

    private AuthMember getAuthMember(Member member) {
        return new AuthMember(Objects.requireNonNull(member.getId()), member.getRole());
    }
}
