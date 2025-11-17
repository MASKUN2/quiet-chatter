package maskun.quietchatter.hexagon.application;

import java.util.List;
import lombok.RequiredArgsConstructor;
import maskun.quietchatter.hexagon.application.value.TalkQueryRequest;
import maskun.quietchatter.hexagon.domain.talk.Talk;
import maskun.quietchatter.hexagon.inbound.TalkQueryable;
import maskun.quietchatter.hexagon.outbound.BookRepository;
import maskun.quietchatter.hexagon.outbound.TalkRepository;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
class TalkQueryService implements TalkQueryable {
    private final BookRepository bookRepository;
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
