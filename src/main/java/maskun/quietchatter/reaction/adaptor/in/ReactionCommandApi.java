package maskun.quietchatter.reaction.adaptor.in;

import lombok.RequiredArgsConstructor;
import maskun.quietchatter.reaction.application.in.ReactionModifiable;
import maskun.quietchatter.reaction.application.in.ReactionTarget;
import maskun.quietchatter.security.AuthMember;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1/reactions")
@RestController
@RequiredArgsConstructor
class ReactionCommandApi {
    private final ReactionModifiable reactionModifiable;

    @PostMapping
    ResponseEntity<Void> add(@AuthenticationPrincipal AuthMember authMember,
                               @RequestBody ReactionWebRequest webRequest) {

        ReactionTarget target = new ReactionTarget(webRequest.talkId(), authMember.id(), webRequest.type());
        reactionModifiable.add(target);
        return ResponseEntity.accepted().build();
    }

    @DeleteMapping
    ResponseEntity<Void> remove(@AuthenticationPrincipal AuthMember authMember,
                                  @RequestBody ReactionWebRequest webRequest) {
        ReactionTarget target = new ReactionTarget(webRequest.talkId(),authMember.id(), webRequest.type());
        reactionModifiable.remove(target);
        return ResponseEntity.accepted().build();
    }
}
