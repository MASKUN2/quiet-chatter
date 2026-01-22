package maskun.quietchatter.talk.adaptor.in;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.UUID;
import maskun.quietchatter.member.domain.Role;
import maskun.quietchatter.reaction.application.in.ReactionQueryable;
import maskun.quietchatter.reaction.domain.Reaction;
import maskun.quietchatter.reaction.domain.Reaction.Type;
import maskun.quietchatter.security.AuthMember;
import maskun.quietchatter.talk.domain.Talk;
import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.util.ReflectionTestUtils;

class TalkResponseMapperTest {

    private final ReactionQueryable reactionQueryable = mock(ReactionQueryable.class);
    private final TalkResponseMapper mapper = new TalkResponseMapper(reactionQueryable);

    @Test
    @DisplayName("인증 정보가 없는 경우 모든 반응은 false")
    void mapToResponse_WithoutAuth() {
        // Given
        List<Talk> talks = Instancio.ofList(Talk.class).size(3).create();
        Page<Talk> page = new PageImpl<>(talks);

        // When
        Page<TalkResponse> result = mapper.mapToResponse(page);

        // Then
        assertThat(result.getContent()).hasSize(3)
                .allSatisfy(response -> {
                    assertThat(response.didILike()).isFalse();
                    assertThat(response.didISupport()).isFalse();
                });
    }

    @Test
    @DisplayName("인증 정보가 있는 경우 내 반응이 매핑된다")
    void mapToResponse_WithAuth() {
        // Given
        AuthMember authMember = new AuthMember(UUID.randomUUID(), Role.REGULAR);
        List<Talk> talks = Instancio.ofList(Talk.class).size(2).create();
        Talk talk1 = talks.get(0);
        Talk talk2 = talks.get(1);
        Page<Talk> page = new PageImpl<>(talks);

        // talk1에 대해 LIKE 반응이 있다고 가정
        Reaction reaction = new Reaction();
        ReflectionTestUtils.setField(reaction, "memberId", authMember.id());
        ReflectionTestUtils.setField(reaction, "talkId", talk1.getId());
        ReflectionTestUtils.setField(reaction, "type", Type.LIKE);

        when(reactionQueryable.getAllBy(eq(authMember.id()), any()))
                .thenReturn(List.of(reaction));

        // When
        Page<TalkResponse> result = mapper.mapToResponse(page, authMember);

        // Then
        assertThat(result.getContent()).hasSize(2);

        // Talk1: LIKE=true
        TalkResponse response1 = result.getContent().stream()
                .filter(r -> r.id().equals(talk1.getId()))
                .findFirst().orElseThrow();
        assertThat(response1.didILike()).isTrue();
        assertThat(response1.didISupport()).isFalse();

        // Talk2: 반응 없음
        TalkResponse response2 = result.getContent().stream()
                .filter(r -> r.id().equals(talk2.getId()))
                .findFirst().orElseThrow();
        assertThat(response2.didILike()).isFalse();
        assertThat(response2.didISupport()).isFalse();
    }
}