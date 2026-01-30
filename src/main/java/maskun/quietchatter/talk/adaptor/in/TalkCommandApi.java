package maskun.quietchatter.talk.adaptor.in;

import lombok.RequiredArgsConstructor;
import maskun.quietchatter.security.domain.AuthMember;
import maskun.quietchatter.talk.application.in.TalkCommandable;
import maskun.quietchatter.talk.application.in.TalkCreateRequest;
import maskun.quietchatter.talk.domain.Talk;
import maskun.quietchatter.web.IdResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.UUID;

@RequestMapping("/api/v1/talks")
@RestController
@RequiredArgsConstructor
class TalkCommandApi {
    private final TalkCommandable talkCommandable;

    @PostMapping
    ResponseEntity<IdResponse> post(@AuthenticationPrincipal AuthMember authMember,
                                    @RequestBody TalkCreateWebRequest webRequest) {
        TalkCreateRequest createRequest = convert(webRequest, authMember.id());
        Talk posted = talkCommandable.create(createRequest);
        IdResponse idResponse = new IdResponse(posted.getId());
        return ResponseEntity.ok(idResponse);
    }

    @PutMapping("/{talkId}")
    ResponseEntity<Void> update(@AuthenticationPrincipal AuthMember authMember,
                                @PathVariable UUID talkId,
                                @RequestBody TalkUpdateWebRequest webRequest) {
        talkCommandable.update(talkId, authMember.id(), webRequest.content());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{talkId}")
    ResponseEntity<Void> delete(@AuthenticationPrincipal AuthMember authMember,
                                @PathVariable UUID talkId) {
        talkCommandable.hide(talkId, authMember.id());
        return ResponseEntity.noContent().build();
    }

    private TalkCreateRequest convert(TalkCreateWebRequest request, UUID memberId) {
        LocalDate dateToHidden = null;
        if (request.hidden() != null) {
            dateToHidden = LocalDate.ofInstant(request.hidden(), ZoneId.systemDefault());
        }
        
        return new TalkCreateRequest(
                request.bookId(),
                memberId,
                request.content(),
                dateToHidden
        );
    }
}
