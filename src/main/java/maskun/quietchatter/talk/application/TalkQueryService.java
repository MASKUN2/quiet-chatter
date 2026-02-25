package maskun.quietchatter.talk.application;

import lombok.RequiredArgsConstructor;
import maskun.quietchatter.talk.application.in.RecommendTalkQueryable;
import maskun.quietchatter.talk.application.in.RecommendTalks;
import maskun.quietchatter.talk.application.in.TalkQueryRequest;
import maskun.quietchatter.talk.application.in.TalkQueryable;
import maskun.quietchatter.talk.application.out.RecommendTalkRepository;
import maskun.quietchatter.talk.application.out.TalkRepository;
import maskun.quietchatter.talk.domain.Talk;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
class TalkQueryService implements TalkQueryable, RecommendTalkQueryable {
    private final TalkRepository talkRepository;
    private final RecommendTalkRepository recommendTalkRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<Talk> findBy(TalkQueryRequest request) {
        return talkRepository.findByBookIdAndIsHiddenFalseOrderByCreatedAtDesc(request.bookId(), request.pageRequest());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Talk> findByMemberId(java.util.UUID memberId, org.springframework.data.domain.Pageable pageRequest) {
        return talkRepository.findByMemberIdAndIsHiddenFalseOrderByCreatedAtDesc(memberId, pageRequest);
    }

    @Override
    @Transactional(readOnly = true)
    public RecommendTalks get() {
        return recommendTalkRepository.get();
    }
}
