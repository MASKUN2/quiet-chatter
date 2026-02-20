package maskun.quietchatter.talk.adaptor.out;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import maskun.quietchatter.talk.application.out.TalkRepository;
import maskun.quietchatter.talk.domain.Talk;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class RandomTalkSampler {
    private final JdbcTemplate jdbcTemplate;
    private final TalkRepository talkRepository;

    List<Talk> sample(int limit) {
        List<UUID> ids = jdbcTemplate.queryForList(
                "SELECT id FROM talk WHERE is_hidden = false ORDER BY RANDOM() LIMIT ?;",
                UUID.class,
                limit);

        return talkRepository.findAllByIdIn(ids);
    }
}
