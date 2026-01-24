package maskun.quietchatter.talk.application;

import maskun.quietchatter.book.application.out.BookRepository;
import maskun.quietchatter.member.application.out.MemberRepository;
import maskun.quietchatter.talk.application.in.TalkCreatable;
import maskun.quietchatter.talk.application.in.TalkCreateRequest;
import maskun.quietchatter.talk.application.out.TalkRepository;
import maskun.quietchatter.talk.domain.Talk;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
class TalkCreateService implements TalkCreatable {
    private final BookRepository bookRepository;
    private final TalkRepository talkRepository;
    private final MemberRepository memberRepository;

    TalkCreateService(BookRepository bookRepository, TalkRepository talkRepository,
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

        Talk talk;
        if (request.dateToHidden() != null) {
            talk = new Talk(request.bookId(), request.memberId(), request.content(), request.dateToHidden());
        } else {
            talk = new Talk(request.bookId(), request.memberId(), request.content());
        }

        return talkRepository.save(talk);
    }

}
