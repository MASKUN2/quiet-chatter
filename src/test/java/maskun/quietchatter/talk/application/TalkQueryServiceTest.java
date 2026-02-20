package maskun.quietchatter.talk.application;

import maskun.quietchatter.talk.application.in.TalkQueryRequest;
import maskun.quietchatter.talk.application.out.TalkRepository;
import maskun.quietchatter.talk.domain.Talk;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TalkQueryServiceTest {
    @Mock
    private TalkRepository talkRepository;

    @Test
    @DisplayName("없는 책을 조회시 빈 페이지 반환")
    void findByNotExistingBook() {
        when(talkRepository.findByBookIdAndIsHiddenFalseOrderByCreatedAtDesc(any(), any())).thenReturn(Page.empty());

        TalkQueryService talkQueryService = new TalkQueryService(talkRepository, null);
        TalkQueryRequest request = new TalkQueryRequest(UUID.randomUUID(), PageRequest.of(0, 10));
        Page<Talk> page = talkQueryService.findBy(request);

        assertThat(page).isEmpty();
    }
}
