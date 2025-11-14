package maskun.quietchatter.adaptor.web.talk;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import maskun.quietchatter.adaptor.web.shared.IdResponse;
import maskun.quietchatter.hexagon.application.value.TalkCreateRequest;
import maskun.quietchatter.hexagon.application.value.TalkQueryRequest;
import maskun.quietchatter.hexagon.domain.talk.Content;
import maskun.quietchatter.hexagon.domain.talk.Talk;
import maskun.quietchatter.hexagon.domain.talk.Time;
import maskun.quietchatter.hexagon.inbound.TalkCreatable;
import maskun.quietchatter.hexagon.inbound.TalkQueryable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/talks")
@RestController
@RequiredArgsConstructor
public class TalkApi {
    private final TalkCreatable talkCreatable;
    private final TalkQueryable talkQueryable;

    @PostMapping
    public ResponseEntity<IdResponse> post(@RequestBody TalkPostRequest webRequest,
                                           @AuthenticationPrincipal UUID memberId) {
        TalkCreateRequest createRequest = convert(webRequest, memberId);
        Talk posted = talkCreatable.create(createRequest);
        IdResponse idResponse = new IdResponse(posted.getId());
        return ResponseEntity.ok(idResponse);
    }

    private TalkCreateRequest convert(TalkPostRequest request, UUID memberId) {
        return new TalkCreateRequest(
                request.bookId(),
                memberId,
                new Content(request.content()),
                new Time(request.hidden())
        );
    }

    @GetMapping
    public ResponseEntity<Page<TalkResponse>> find(@RequestParam("bookId") UUID bookId,
                                                   @PageableDefault Pageable pageable) {
        TalkQueryRequest request = new TalkQueryRequest(bookId, pageable);
        Page<TalkResponse> page = talkQueryable.findBy(request)
                .map(TalkResponse::from);
        return ResponseEntity.ok(page);
    }
}
