package maskun.quietchatter.talk.application;

import lombok.RequiredArgsConstructor;
import maskun.quietchatter.book.application.out.BookRepository;
import maskun.quietchatter.member.application.out.MemberRepository;
import maskun.quietchatter.talk.application.in.TalkCommandable;
import maskun.quietchatter.talk.application.in.TalkCreateRequest;
import maskun.quietchatter.talk.application.out.TalkRepository;
import maskun.quietchatter.talk.domain.Talk;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
class TalkCommandService implements TalkCommandable {
    private final BookRepository bookRepository;
    private final TalkRepository talkRepository;
    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public Talk create(TalkCreateRequest request) {
        bookRepository.require(request.bookId());
        maskun.quietchatter.member.domain.Member member = memberRepository.require(request.memberId());

        Talk talk = new Talk(request.bookId(), request.memberId(), member.getNickname(), request.content(), request.dateToHidden());

        return talkRepository.save(talk);
    }

    @Override
    @Transactional
    public void update(UUID talkId, UUID memberId, String content) {
        Talk talk = talkRepository.require(talkId);
        talk.validateOwner(memberId);
        talk.updateContent(content);
    }

    @Override
    @Transactional
    public void hide(UUID talkId, UUID memberId) {
        Talk talk = talkRepository.require(talkId);
        talk.validateOwner(memberId);
        talk.hide();
    }
}
