package maskun.quietchatter.hexagon.application;

import maskun.quietchatter.hexagon.application.value.TalkCreateRequest;
import maskun.quietchatter.hexagon.domain.talk.Talk;
import maskun.quietchatter.hexagon.inbound.TalkCreatable;
import maskun.quietchatter.hexagon.outbound.BookRepository;
import maskun.quietchatter.hexagon.outbound.MemberRepository;
import maskun.quietchatter.hexagon.outbound.TalkRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TalkCreateService implements TalkCreatable {
    private final BookRepository bookRepository;
    private final TalkRepository talkRepository;
    private final MemberRepository memberRepository;

    public TalkCreateService(BookRepository bookRepository, TalkRepository talkRepository,
                             MemberRepository memberRepository) {
        this.bookRepository = bookRepository;
        this.talkRepository = talkRepository;
        this.memberRepository = memberRepository;
    }

    @Override
    @Transactional
    public Talk create(TalkCreateRequest request) {
        bookRepository.require(request.bookId());
        memberRepository.require(request.memberId());

        Talk talk = new Talk();
        talk.updateBookId(request.bookId());
        talk.updateMemberId(request.memberId());
        talk.update(request.content());
        talk.update(request.time());

        return talkRepository.save(talk);
    }

}
