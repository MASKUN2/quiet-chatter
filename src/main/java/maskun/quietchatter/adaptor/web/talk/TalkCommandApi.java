package maskun.quietchatter.adaptor.web.talk;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import maskun.quietchatter.adaptor.web.shared.IdResponse;
import maskun.quietchatter.hexagon.application.value.TalkCreateRequest;
import maskun.quietchatter.hexagon.domain.talk.Content;
import maskun.quietchatter.hexagon.domain.talk.Talk;
import maskun.quietchatter.hexagon.domain.talk.Time;
import maskun.quietchatter.hexagon.inbound.TalkCreatable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/talks")
@RestController
@RequiredArgsConstructor
public class TalkCommandApi {
    private final TalkCreatable talkCreatable;

    @PostMapping
    public ResponseEntity<IdResponse> post(@RequestBody TalkCreateWebRequest webRequest,
                                           @AuthenticationPrincipal UUID memberId) {
        TalkCreateRequest createRequest = convert(webRequest, memberId);
        Talk posted = talkCreatable.create(createRequest);
        IdResponse idResponse = new IdResponse(posted.getId());
        return ResponseEntity.ok(idResponse);
    }

    private TalkCreateRequest convert(TalkCreateWebRequest request, UUID memberId) {
        return new TalkCreateRequest(
                request.bookId(),
                memberId,
                new Content(request.content()),
                new Time(request.hidden())
        );
    }
}
