package maskun.quietchatter.talk.application.out;

import java.time.LocalDate;
import maskun.quietchatter.talk.domain.Talk;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

public interface TalkBulkRepository extends Repository<Talk, java.util.UUID> {

    @Modifying
    @Query("UPDATE talk t SET t.isHidden = true WHERE t.dateToHidden <= :now AND t.isHidden = false")
    int hideExpiredTalks(@Param("now") LocalDate now);
}
