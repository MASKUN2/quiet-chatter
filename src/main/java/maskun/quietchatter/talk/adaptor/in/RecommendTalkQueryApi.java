package maskun.quietchatter.talk.adaptor.in;

import lombok.RequiredArgsConstructor;
import maskun.quietchatter.talk.application.in.RecommendTalkQueryable;
import maskun.quietchatter.talk.domain.Talk;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/v1/talks")
@RestController
@RequiredArgsConstructor
class RecommendTalkQueryApi {
    private final RecommendTalkQueryable recommendTalkQueryable;

    @GetMapping("/recommend")
    ResponseEntity<List<TalkResponse>> getRecent() {
        List<Talk> talks = recommendTalkQueryable.get().items();
        List<TalkResponse> responses = talks.stream().map(talk -> new TalkResponse(
                talk.getId(),
                talk.getBookId(),
                talk.getMemberId(),
                talk.getNickname(),
                talk.getCreatedAt(),
                talk.getDateToHidden(),
                talk.getContent(),
                talk.getLikeCount(),
                false,
                talk.getSupportCount(),
                false,
                talk.isModified()
        )).toList();
        return ResponseEntity.ok(responses);
    }
}
