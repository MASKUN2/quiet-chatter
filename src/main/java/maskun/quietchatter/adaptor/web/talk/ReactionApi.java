package maskun.quietchatter.adaptor.web.talk;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import maskun.quietchatter.hexagon.domain.reaction.Reaction.Type;
import maskun.quietchatter.hexagon.inbound.TalkReactable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/talks/{talkId}/reactions")
@RestController
@RequiredArgsConstructor
public class ReactionApi {
    private final TalkReactable talkReactable;

    @PostMapping
    public ResponseEntity<String> post(@PathVariable("talkId") UUID talkId, @AuthenticationPrincipal UUID memberId,
                                       @RequestBody ReactionWebRequest webRequest) {
        Type type = webRequest.type();
        talkReactable.add(talkId, memberId, type);
        return ResponseEntity.accepted().build();
    }

    @DeleteMapping
    public ResponseEntity<String> delete(@PathVariable("talkId") UUID talkId, @AuthenticationPrincipal UUID memberId,
                                         @RequestBody ReactionWebRequest webRequest) {
        Type type = webRequest.type();
        talkReactable.remove(talkId, memberId, type);
        return ResponseEntity.accepted().build();
    }
}
