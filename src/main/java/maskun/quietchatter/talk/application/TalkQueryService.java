package maskun.quietchatter.talk.application;

import java.util.List;
import lombok.RequiredArgsConstructor;
import maskun.quietchatter.talk.application.in.TalkQueryRequest;
import maskun.quietchatter.talk.application.in.TalkQueryable;
import maskun.quietchatter.talk.application.out.TalkRepository;
import maskun.quietchatter.talk.domain.Talk;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
class TalkQueryService implements TalkQueryable {
    private final TalkRepository talkRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<Talk> findBy(TalkQueryRequest request) {
        return talkRepository.findByBookIdOrderByCreatedAtDesc(request.bookId(), request.pageRequest());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Talk> getRecent(Limit limit) {
        return talkRepository.findByOrderByCreatedAtDesc(limit);
    }
}
