package maskun.quietchatter.talk.adaptor.in;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import maskun.quietchatter.security.AuthMember;
import maskun.quietchatter.shared.web.IdResponse;
import maskun.quietchatter.talk.application.in.TalkCreatable;
import maskun.quietchatter.talk.application.in.TalkCreateRequest;
import maskun.quietchatter.talk.domain.Content;
import maskun.quietchatter.talk.domain.Talk;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/talks")
@RestController
@RequiredArgsConstructor
class TalkCommandApi {
    private final TalkCreatable talkCreatable;

    @PostMapping
    ResponseEntity<IdResponse> post(@AuthenticationPrincipal AuthMember authMember,
                                    @RequestBody TalkCreateWebRequest webRequest) {
        TalkCreateRequest createRequest = convert(webRequest, authMember.id());
        Talk posted = talkCreatable.create(createRequest);
        IdResponse idResponse = new IdResponse(posted.getId());
        return ResponseEntity.ok(idResponse);
    }

    private TalkCreateRequest convert(TalkCreateWebRequest request, UUID memberId) {
        LocalDate dateToHidden = null;
        if (request.hidden() != null) {
            dateToHidden = LocalDate.ofInstant(request.hidden(), ZoneId.systemDefault());
        }
        
        return new TalkCreateRequest(
                request.bookId(),
                memberId,
                new Content(request.content()),
                dateToHidden
        );
    }
}
