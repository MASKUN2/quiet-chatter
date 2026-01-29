package maskun.quietchatter.talk.adaptor.in;

import maskun.quietchatter.shared.web.WebConfig;
import maskun.quietchatter.talk.application.in.RecommendTalkQueryable;
import maskun.quietchatter.talk.application.in.RecommendTalks;
import maskun.quietchatter.talk.domain.Talk;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@SuppressWarnings("SameParameterValue")
@WebMvcTest(controllers = RecommendTalkQueryApi.class,
        excludeAutoConfiguration = SecurityAutoConfiguration.class)
@Import(WebConfig.class)
class RecommendTalkQueryApiTest {
    @MockitoBean
    private RecommendTalkQueryable recommendTalkQueryable;

    @Autowired
    private MockMvcTester tester;

    @BeforeEach
    void setUp() {

        List<Talk> talks = Instancio.ofList(Talk.class)
                .size(RecommendTalks.MAX_SIZE)
                .create();

        when(recommendTalkQueryable.get())
                .thenReturn(new RecommendTalks(talks));
    }

    @Test
    @DisplayName("최근 조회")
    void getRecent() {
        MvcTestResult result = tester.get()
                .uri("/api/v1/talks/recommend")
                .contentType(MediaType.APPLICATION_JSON)
                .exchange();

        assertThat(result)
                .hasStatus(HttpStatus.OK)
                .bodyJson()
                .extractingPath("$")
                .asArray()
                .hasSize(RecommendTalks.MAX_SIZE)
                .allSatisfy(element -> {
                    assertThat(element).extracting("id").isNotNull();
                    assertThat(element).extracting("bookId").isNotNull();
                    assertThat(element).extracting("memberId").isNotNull();
                    assertThat(element).extracting("content").isNotNull();
                    assertThat(element).extracting("like_count").isNotNull();
                    assertThat(element).extracting("support_count").isNotNull();
                });
    }

}
