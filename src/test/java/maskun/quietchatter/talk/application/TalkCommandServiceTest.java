package maskun.quietchatter.talk.application;

import maskun.quietchatter.book.application.out.BookRepository;
import maskun.quietchatter.book.domain.Book;
import maskun.quietchatter.member.application.out.MemberRepository;
import maskun.quietchatter.member.domain.Member;
import maskun.quietchatter.talk.application.in.TalkCreateRequest;
import maskun.quietchatter.talk.application.out.TalkRepository;
import maskun.quietchatter.talk.domain.Talk;
import org.instancio.Instancio;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.stubbing.Answer;

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TalkCommandServiceTest {
    private TalkRepository talkRepository;
    private BookRepository bookRepository;
    private MemberRepository memberRepository;
    private TalkCommandService talkCommandService;

    @BeforeEach
    void setUp() {
        talkRepository = mock(TalkRepository.class);
        bookRepository = mock(BookRepository.class);
        memberRepository = mock(MemberRepository.class);
        talkCommandService = new TalkCommandService(bookRepository, talkRepository, memberRepository);
    }

    @Test
    @DisplayName("북톡 등록 성공")
    void create() {
        UUID bookId = UUID.randomUUID();
        UUID memberId = UUID.randomUUID();
        String content = "test content";
        String nickname = "testUser";
        TalkCreateRequest request = new TalkCreateRequest(bookId, memberId, content, LocalDate.now().plusMonths(1));

        when(bookRepository.require(any())).thenReturn(Instancio.create(Book.class));

        Member member = mock(Member.class);
        given(member.getNickname()).willReturn(nickname);
        when(memberRepository.require(any())).thenReturn(member);
        
        when(talkRepository.save(any())).thenAnswer(populateTalk());

        //when
        Talk created = talkCommandService.create(request);

        //then
        assertThat(created.getId()).isNotNull();
        assertThat(created.getBookId()).isNotNull();
        assertThat(created.getMemberId()).isNotNull();
        assertThat(created.getContent()).isEqualTo(content);
        assertThat(created.getNickname()).isEqualTo(nickname);
        assertThat(created.getCreatedAt()).isNotNull();
    }

    @DisplayName("본인의 톡 내용을 수정할 수 있다")
    @Test
    void updateTalk() {
        // given
        UUID talkId = UUID.randomUUID();
        UUID memberId = UUID.randomUUID();
        Talk talk = new Talk(UUID.randomUUID(), memberId, "nick", "old content");
        given(talkRepository.require(talkId)).willReturn(talk);

        // when
        talkCommandService.update(talkId, memberId, "new content");

        // then
        assertThat(talk.getContent()).isEqualTo("new content");
    }

    @DisplayName("타인의 톡을 수정하려고 하면 예외가 발생한다")
    @Test
    void updateTalkByOtherMember() {
        // given
        UUID talkId = UUID.randomUUID();
        UUID ownerId = UUID.randomUUID();
        UUID otherId = UUID.randomUUID();
        Talk talk = new Talk(UUID.randomUUID(), ownerId, "nick", "content");
        given(talkRepository.require(talkId)).willReturn(talk);

        // when & then
        assertThatThrownBy(() -> talkCommandService.update(talkId, otherId, "new content"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("본인의 톡만 수정/삭제할 수 있습니다.");
    }

    @DisplayName("본인의 톡을 숨길 수 있다")
    @Test
    void hideTalk() {
        // given
        UUID talkId = UUID.randomUUID();
        UUID memberId = UUID.randomUUID();
        Talk talk = new Talk(UUID.randomUUID(), memberId, "nick", "content");
        given(talkRepository.require(talkId)).willReturn(talk);

        // when
        talkCommandService.hide(talkId, memberId);

        // then
        assertThat(talk.isHidden()).isTrue();
    }

    private static @NotNull Answer<Object> populateTalk() {
        return invocation -> {
            Talk talk = invocation.getArgument(0);
            Instancio.ofObject(talk).fill();
            return talk;
        };
    }
}
