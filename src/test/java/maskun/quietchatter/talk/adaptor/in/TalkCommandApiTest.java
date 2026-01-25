package maskun.quietchatter.talk.adaptor.in;

import static org.assertj.core.api.Assertions.*;
import static org.instancio.Instancio.create;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.UUID;
import maskun.quietchatter.member.domain.Role;
import maskun.quietchatter.security.AuthMember;
import maskun.quietchatter.shared.web.WebConfig;
import maskun.quietchatter.talk.application.in.TalkCreatable;
import maskun.quietchatter.talk.application.in.TalkUpdatable;
import maskun.quietchatter.talk.domain.Talk;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;

@WebMvcTest(controllers = TalkCommandApi.class)
@Import({WebConfig.class})
class TalkCommandApiTest {

    @MockitoBean
    private TalkCreatable talkCreatable;

    @MockitoBean
    private TalkUpdatable talkUpdatable;

    @Autowired
    private MockMvcTester tester;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        when(talkCreatable.create(any())).thenReturn(create(Talk.class));

        AuthMember authMember = new AuthMember(UUID.randomUUID(), Role.GUEST);
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                authMember, null, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    @DisplayName("북톡 등록 성공")
    void post() throws JsonProcessingException {
        TalkCreateWebRequest request = create(TalkCreateWebRequest.class);

        //when
        MvcTestResult result = tester.post().uri("/api/talks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .exchange();

        //then
        assertThat(result).hasStatusOk()
                .bodyJson()
                .extractingPath("$.id").isNotNull();
    }

    @Test
    @DisplayName("북톡 수정 성공")
    void update() throws JsonProcessingException {
        TalkUpdateWebRequest request = new TalkUpdateWebRequest("updated content");

        //when
        MvcTestResult result = tester.put().uri("/api/talks/{talkId}", UUID.randomUUID())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .exchange();

        //then
        assertThat(result).hasStatus(HttpStatus.NO_CONTENT);
    }

    @Test
    @DisplayName("북톡 삭제(숨김) 성공")
    void delete() {
        //when
        MvcTestResult result = tester.delete().uri("/api/talks/{talkId}", UUID.randomUUID())
                .exchange();

        //then
        assertThat(result).hasStatus(HttpStatus.NO_CONTENT);
    }
}
