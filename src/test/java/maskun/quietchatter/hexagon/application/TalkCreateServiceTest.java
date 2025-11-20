package maskun.quietchatter.hexagon.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.UUID;
import maskun.quietchatter.hexagon.application.value.TalkCreateRequest;
import maskun.quietchatter.hexagon.domain.book.Book;
import maskun.quietchatter.hexagon.domain.talk.Content;
import maskun.quietchatter.hexagon.domain.talk.Talk;
import maskun.quietchatter.hexagon.domain.talk.Time;
import maskun.quietchatter.hexagon.outbound.BookRepository;
import maskun.quietchatter.hexagon.outbound.TalkRepository;
import maskun.quietchatter.member.application.out.MemberRepository;
import maskun.quietchatter.member.domain.Member;
import org.instancio.Instancio;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
@ActiveProfiles("test")
class TalkCreateServiceTest {
    @MockitoBean
    private TalkRepository talkRepository;

    @MockitoBean
    private BookRepository bookRepository;

    @MockitoBean
    private MemberRepository memberRepository;

    @Autowired
    private TalkCreateService talkCreateService;

    @Test
    void create() {
        UUID bookId = UUID.randomUUID();
        UUID memberId = UUID.randomUUID();
        Content content = Instancio.create(Content.class);
        TalkCreateRequest request = new TalkCreateRequest(bookId, memberId, content, new Time(Instant.now()));

        when(bookRepository.require(any())).thenReturn(Instancio.create(Book.class));
        when(memberRepository.require(any())).thenReturn(Instancio.create(Member.class));

        when(talkRepository.save(any())).thenAnswer(populateTalk());

        //when
        Talk created = talkCreateService.create(request);

        //then
        assertThat(created.getId()).isNotNull();
        assertThat(created.getBookId()).isNotNull();
        assertThat(created.getMemberId()).isNotNull();
        assertThat(created.getContent()).isEqualTo(content);
        assertThat(created.getCreatedAt()).isNotNull();
    }

    private static @NotNull Answer<Object> populateTalk() {
        return invocation -> {
            Talk talk = invocation.getArgument(0);
            Instancio.ofObject(talk).fill();
            return talk;
        };
    }
}
