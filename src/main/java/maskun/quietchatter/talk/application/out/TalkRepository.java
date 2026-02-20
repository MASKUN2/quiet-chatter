package maskun.quietchatter.talk.application.out;

import maskun.quietchatter.member.domain.Member;
import maskun.quietchatter.talk.domain.Talk;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.Repository;

import java.util.*;

public interface TalkRepository extends Repository<Talk, UUID> {
    @SuppressWarnings("UnusedReturnValue")
    default Talk require(UUID id) {
        return findById(id).orElseThrow(() ->
                new NoSuchElementException("찾을 수 없음 [%s] id=%s".formatted(Member.class.getSimpleName(), id)));
    }

    Talk save(Talk talk);

    void saveAll(Iterable<Talk> talks);

    Page<Talk> findByBookIdAndIsHiddenFalseOrderByCreatedAtDesc(UUID bookId, Pageable pageRequest);

    Optional<Talk> findById(UUID id);

    List<Talk> findAllByIdIn(Collection<UUID> ids);
}
