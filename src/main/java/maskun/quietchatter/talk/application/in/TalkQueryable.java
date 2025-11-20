package maskun.quietchatter.talk.application.in;

import maskun.quietchatter.talk.domain.Talk;
import org.springframework.data.domain.Page;

public interface TalkQueryable {
    Page<Talk> findBy(TalkQueryRequest request);
}
