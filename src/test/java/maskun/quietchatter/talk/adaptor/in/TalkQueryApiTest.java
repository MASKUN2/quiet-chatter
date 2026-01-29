package maskun.quietchatter.talk.adaptor.in;

import maskun.quietchatter.member.domain.Role;
import maskun.quietchatter.security.AuthMember;
import maskun.quietchatter.security.AuthMemberToken;
import maskun.quietchatter.talk.application.in.TalkQueryable;
import maskun.quietchatter.web.WebConfig;
import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@WebMvcTest(controllers = TalkQueryApi.class)
@Import(WebConfig.class)
class TalkQueryApiTest {

    @MockitoBean
    private TalkQueryable talkQueryable;
    @MockitoBean
    private TalkResponseMapper mapper;
    @Autowired
    private MockMvcTester tester;

    @Test
    @DisplayName("페이징 조회 - Mapper 위임 확인")
    void getByPage() {
        // Given
        UUID bookId = UUID.randomUUID();
        Page<TalkResponse> responsePage = new PageImpl<>(Instancio.ofList(TalkResponse.class).size(5).create());

        when(talkQueryable.findBy(any())).thenReturn(Page.empty());
        when(mapper.mapToResponse(any(), any(AuthMember.class))).thenReturn(responsePage);

        // When & Then
        tester.get().uri("/api/v1/talks")
                .queryParam("bookId", bookId.toString())
                .queryParam("page", "0")
                .queryParam("size", "10")
                .with(authentication(new AuthMemberToken(new AuthMember(UUID.randomUUID(), Role.REGULAR))))
                .with(csrf())
                .exchange()
                .assertThat()
                .hasStatusOk()
                .bodyJson()
                .hasPathSatisfying("$.page.totalElements", v -> assertThat(v).isEqualTo(5))
                .hasPathSatisfying("$.content", v -> assertThat(v).isNotNull());
    }
}