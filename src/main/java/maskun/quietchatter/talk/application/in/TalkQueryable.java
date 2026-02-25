package maskun.quietchatter.talk.application.in;

import java.util.UUID;

import org.springframework.data.domain.Page;

import maskun.quietchatter.talk.domain.Talk;

public interface TalkQueryable {
    Page<Talk> findBy(TalkQueryRequest request);
    Page<Talk> findByMemberId(UUID memberId, org.springframework.data.domain.Pageable pageRequest);
}
