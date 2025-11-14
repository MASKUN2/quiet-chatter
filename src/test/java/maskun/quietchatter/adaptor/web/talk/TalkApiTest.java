package maskun.quietchatter.adaptor.web.talk;

import static maskun.quietchatter.hexagon.domain.Fixture.book;
import static maskun.quietchatter.hexagon.domain.Fixture.talk;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import maskun.quietchatter.adaptor.web.WebConfig;
import maskun.quietchatter.hexagon.application.value.TalkQueryRequest;
import maskun.quietchatter.hexagon.domain.book.Book;
import maskun.quietchatter.hexagon.domain.talk.Content;
import maskun.quietchatter.hexagon.domain.talk.Talk;
import maskun.quietchatter.hexagon.inbound.TalkCreatable;
import maskun.quietchatter.hexagon.inbound.TalkQueryable;
import net.datafaker.Faker;
import org.instancio.Instancio;
import org.instancio.Model;
import org.instancio.Select;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;
import org.springframework.web.util.UriComponentsBuilder;

@WebMvcTest(controllers = TalkApi.class,
        excludeAutoConfiguration = SecurityAutoConfiguration.class)
@Import(WebConfig.class)
class TalkApiTest {

    @MockitoBean
    private TalkCreatable talkCreatable;

    @MockitoBean
    private TalkQueryable talkQueryable;

    @Autowired
    private TalkApi talkApi;

    @Autowired
    private MockMvcTester tester;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("success post")
    void post() throws JsonProcessingException {
        Book book = book().asPersisted().create();

        UUID bookId = book.getId();
        String text = new Faker().text().text(100);
        Content content = new Content(text);

        Talk talk = talk().asPersisted(book)
                .set(Select.field(Talk::getContent), content)
                .create();

        when(talkCreatable.create(any()))
                .thenReturn(talk);

        TalkPostRequest request = new TalkPostRequest(bookId, text, Instant.now());

        //when
        MvcTestResult result = tester.post().uri("/api/talks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .exchange();

        //then
        assertThat(result).hasStatusOk()
                .bodyJson()
                .extractingPath("$.id").isEqualTo(talk.getId().toString());
    }

    @Test
    @DisplayName("page success")
    void findPage() {
        Book book = book().asPersisted().create();

        Model<Talk> talkModel = talk().asPersisted(book)
                .set(Select.field(Talk::getContent), new Content(new Faker().text().text(100)))
                .toModel();

        List<Talk> talks = Instancio.ofList(talkModel).size(10).create();

        TalkQueryRequest request = new TalkQueryRequest(book.getId(), PageRequest.of(0, 10));
        when(talkQueryable.findBy(eq(request)))
                .thenReturn(new PageImpl<>(talks, request.pageRequest(), talks.size()));

        //when
        String uri = UriComponentsBuilder.fromPath("/api/talks")
                .queryParam("size", 10)
                .queryParam("page", 0)
                .queryParam("bookId", book.getId().toString())
                .toUriString();
        MvcTestResult result = tester.get().uri(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange();

        //then
        assertThat(result).hasStatusOk()
                .bodyJson()
                .hasPathSatisfying("$.page.totalElements", value -> assertThat(value).isEqualTo(10))
                .hasPathSatisfying("$.page.totalPages", value -> assertThat(value).isEqualTo(1))
                .hasPathSatisfying("$.page.number", value -> assertThat(value).isEqualTo(0))
                .hasPathSatisfying("$.content[*].content", value -> assertThat(value).isNotEmpty())
                .hasPathSatisfying("$.content[*].bookId", value -> assertThat(value).isNotEmpty())
                .hasPathSatisfying("$.content[*].id", value -> assertThat(value).isNotEmpty());
    }
}
