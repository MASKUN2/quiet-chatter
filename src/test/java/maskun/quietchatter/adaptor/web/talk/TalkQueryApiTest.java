package maskun.quietchatter.adaptor.web.talk;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.UUID;
import maskun.quietchatter.adaptor.web.WebConfig;
import maskun.quietchatter.hexagon.application.value.TalkQueryRequest;
import maskun.quietchatter.hexagon.domain.talk.Talk;
import maskun.quietchatter.hexagon.inbound.ReactionQueryable;
import maskun.quietchatter.hexagon.inbound.TalkQueryable;
import org.instancio.Instancio;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;
import org.springframework.web.util.UriComponentsBuilder;

@WebMvcTest(controllers = TalkQueryApi.class,
        excludeAutoConfiguration = SecurityAutoConfiguration.class)
@Import(WebConfig.class)
class TalkQueryApiTest {
    @MockitoBean
    private TalkQueryable talkQueryable;

    @MockitoBean
    private ReactionQueryable reactionQueryable;

    @Autowired
    private MockMvcTester tester;

    final UUID bookId = UUID.randomUUID();

    final int pageSize = 10;
    final int pageNumber = 0;
    final int recentLimit = 4;

    @BeforeEach
    void setUp() {
        List<Talk> talks = getTalkFixtures(bookId, pageSize);

        TalkQueryRequest request = new TalkQueryRequest(bookId, PageRequest.of(pageNumber, pageSize));

        when(talkQueryable.findBy(eq(request)))
                .thenReturn(new PageImpl<>(talks, request.pageRequest(), talks.size()));

        when(talkQueryable.getRecent(eq(Limit.of(recentLimit))))
                .thenReturn(getTalkFixtures(recentLimit));
    }

    @Test
    @DisplayName("최근 조회")
    void getRecent() {
        MvcTestResult result = tester.get()
                .uri(getRecentUri())
                .contentType(MediaType.APPLICATION_JSON)
                .exchange();

        assertThat(result)
                .hasStatus(HttpStatus.OK)
                .bodyJson()
                .extractingPath("$")
                .asArray()
                .hasSize(recentLimit)
                .allSatisfy(element -> {
                    assertThat(element).extracting("id").isNotNull();
                    assertThat(element).extracting("bookId").isNotNull();
                    assertThat(element).extracting("memberId").isNotNull();
                    assertThat(element).extracting("content").isNotNull();
                    assertThat(element).extracting("like_count").isNotNull();
                    assertThat(element).extracting("support_count").isNotNull();
                });
    }

    @Test
    @DisplayName("페이징 조회")
    void getByPage() {
        MvcTestResult result = tester.get()
                .uri(getPageUri())
                .contentType(MediaType.APPLICATION_JSON)
                .exchange();

        assertThat(result).hasStatusOk()
                .bodyJson()
                .hasPathSatisfying("$.page.totalElements", value -> assertThat(value).isEqualTo(10))
                .hasPathSatisfying("$.page.totalPages", value -> assertThat(value).isEqualTo(1))
                .hasPathSatisfying("$.page.number", value -> assertThat(value).isEqualTo(0))
                .hasPathSatisfying("$.content[*].content", value -> assertThat(value).isNotEmpty())
                .hasPathSatisfying("$.content[*].bookId", value -> assertThat(value).isNotEmpty())
                .hasPathSatisfying("$.content[*].id", value -> assertThat(value).isNotEmpty());
    }

    private String getRecentUri() {
        return UriComponentsBuilder.fromPath("/api/talks")
                .queryParam("recent-limit", recentLimit)
                .toUriString();
    }

    private @NotNull String getPageUri() {
        return UriComponentsBuilder.fromPath("/api/talks")
                .queryParam("size", pageSize)
                .queryParam("page", pageNumber)
                .queryParam("bookId", bookId)
                .toUriString();
    }

    private static List<Talk> getTalkFixtures(UUID bookId, int size) {
        return Instancio.ofList(Talk.class)
                .size(size)
                .set(field(Talk::getBookId), bookId)
                .create();
    }

    private static List<Talk> getTalkFixtures(int size) {
        return Instancio.ofList(Talk.class)
                .size(size)
                .create();
    }
}
