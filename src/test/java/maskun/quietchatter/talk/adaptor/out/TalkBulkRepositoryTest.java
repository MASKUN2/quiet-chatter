package maskun.quietchatter.talk.adaptor.out;

import static org.assertj.core.api.Assertions.*;
import static org.instancio.Select.field;
import static org.instancio.Select.fields;

import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.util.List;
import maskun.quietchatter.WithTestContainerDatabases;
import maskun.quietchatter.shared.persistence.BaseEntity;
import maskun.quietchatter.shared.persistence.JpaConfig;
import maskun.quietchatter.talk.application.out.TalkBulkRepository;
import maskun.quietchatter.talk.application.out.TalkRepository;
import maskun.quietchatter.talk.domain.Talk;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@Import(JpaConfig.class)
class TalkBulkRepositoryTest implements WithTestContainerDatabases {

    @Autowired
    private TalkBulkRepository talkBulkRepository;

    @Autowired
    private TalkRepository talkRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void hideExpiredTalks() {
        // Given
        LocalDate now = LocalDate.now();
        
        // 1. 만료된 톡 (숨겨져야 함)
        Talk expiredTalk = Instancio.of(Talk.class)
                .ignore(fields().declaredIn(BaseEntity.class))
                .set(field(Talk::getDateToHidden), now.minusDays(1))
                .set(field(Talk::isHidden), false)
                .create();
        
        // 2. 오늘 만료되는 톡 (숨겨져야 함)
        Talk expiringTodayTalk = Instancio.of(Talk.class)
                .ignore(fields().declaredIn(BaseEntity.class))
                .set(field(Talk::getDateToHidden), now)
                .set(field(Talk::isHidden), false)
                .create();

        // 3. 미래에 만료되는 톡 (숨겨지면 안 됨)
        Talk futureTalk = Instancio.of(Talk.class)
                .ignore(fields().declaredIn(BaseEntity.class))
                .set(field(Talk::getDateToHidden), now.plusDays(1))
                .set(field(Talk::isHidden), false)
                .create();

        // 4. 이미 숨겨진 톡 (영향 없어야 함)
        Talk alreadyHiddenTalk = Instancio.of(Talk.class)
                .ignore(fields().declaredIn(BaseEntity.class))
                .set(field(Talk::getDateToHidden), now.minusDays(1))
                .set(field(Talk::isHidden), true)
                .create();

        talkRepository.saveAll(List.of(expiredTalk, expiringTodayTalk, futureTalk, alreadyHiddenTalk));

        // When
        int updatedCount = talkBulkRepository.hideExpiredTalks(now);

        entityManager.clear();

        // Then
        assertThat(updatedCount).isEqualTo(2);
        
        assertThat(talkRepository.findById(expiredTalk.getId()).get().isHidden()).isTrue();
        assertThat(talkRepository.findById(expiringTodayTalk.getId()).get().isHidden()).isTrue();
        assertThat(talkRepository.findById(futureTalk.getId()).get().isHidden()).isFalse();
        assertThat(talkRepository.findById(alreadyHiddenTalk.getId()).get().isHidden()).isTrue();
    }
}
