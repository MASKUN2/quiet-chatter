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
import maskun.quietchatter.security.AuthMemberToken;
import maskun.quietchatter.shared.web.WebConfig;
import maskun.quietchatter.talk.application.in.TalkCommandable;
import maskun.quietchatter.talk.domain.Talk;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;

@WebMvcTest(controllers = TalkCommandApi.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
@Import({WebConfig.class})
class TalkCommandApiTest {

    @MockitoBean
    private TalkCommandable talkCommandable;

    @Autowired
    private MockMvcTester tester;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("북톡 등록 성공")
    void post() throws JsonProcessingException {
        TalkCreateWebRequest request = create(TalkCreateWebRequest.class);
        when(talkCommandable.create(any())).thenReturn(create(Talk.class));

        //when
        MvcTestResult result = tester.post().uri("/api/talks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .with(SecurityMockMvcRequestPostProcessors.authentication(
                        new AuthMemberToken(new AuthMember(UUID.randomUUID(), Role.GUEST))))
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
                .with(SecurityMockMvcRequestPostProcessors.authentication(
                        new AuthMemberToken(new AuthMember(UUID.randomUUID(), Role.GUEST))))
                .exchange();

        //then
        assertThat(result).hasStatus(HttpStatus.NO_CONTENT);
    }

    @Test
    @DisplayName("북톡 삭제(숨김) 성공")
    void delete() {
        //when
        MvcTestResult result = tester.delete().uri("/api/talks/{talkId}", UUID.randomUUID())
                .with(SecurityMockMvcRequestPostProcessors.authentication(
                        new AuthMemberToken(new AuthMember(UUID.randomUUID(), Role.GUEST))))
                .exchange();

        //then
        assertThat(result).hasStatus(HttpStatus.NO_CONTENT);
    }
}
