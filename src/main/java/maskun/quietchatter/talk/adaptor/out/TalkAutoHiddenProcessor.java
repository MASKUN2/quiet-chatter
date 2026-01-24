package maskun.quietchatter.talk.adaptor.out;

import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import maskun.quietchatter.talk.application.out.TalkBulkRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class TalkAutoHiddenProcessor {

    private final TalkBulkRepository talkBulkRepository;

    @Transactional
    @Scheduled(cron = "0 0 0 * * *")
    public void process() {
        log.info("Starting auto hidden process for talks...");
        int updatedCount = talkBulkRepository.hideExpiredTalks(LocalDate.now());
        log.info("Auto hidden process finished. Updated {} talks.", updatedCount);
    }
}
