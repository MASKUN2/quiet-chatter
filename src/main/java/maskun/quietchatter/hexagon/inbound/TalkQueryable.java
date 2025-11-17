package maskun.quietchatter.hexagon.inbound;

import java.util.List;
import maskun.quietchatter.hexagon.application.value.TalkQueryRequest;
import maskun.quietchatter.hexagon.domain.talk.Talk;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Page;

public interface TalkQueryable {
    Page<Talk> findBy(TalkQueryRequest request);

    List<Talk> getRecent(Limit limit);
}
