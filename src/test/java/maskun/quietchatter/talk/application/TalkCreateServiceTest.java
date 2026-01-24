package maskun.quietchatter.talk.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.UUID;
import maskun.quietchatter.book.application.out.BookRepository;
import maskun.quietchatter.book.domain.Book;
import maskun.quietchatter.member.application.out.MemberRepository;
import maskun.quietchatter.member.domain.Member;
import maskun.quietchatter.talk.application.in.TalkCreateRequest;
import maskun.quietchatter.talk.application.out.TalkRepository;
import maskun.quietchatter.talk.domain.Content;
import maskun.quietchatter.talk.domain.Talk;
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
        TalkCreateRequest request = new TalkCreateRequest(bookId, memberId, content, LocalDate.now().plusMonths(1));

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
