package maskun.quietchatter.reaction.adaptor.in;

import lombok.RequiredArgsConstructor;
import maskun.quietchatter.reaction.application.in.ReactionModifiable;
import maskun.quietchatter.reaction.application.in.ReactionTarget;
import maskun.quietchatter.security.AuthMember;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/reactions")
@RestController
@RequiredArgsConstructor
class ReactionCommandApi {
    private final ReactionModifiable reactionModifiable;

    @PostMapping
    ResponseEntity<String> add(@AuthenticationPrincipal AuthMember authMember,
                               @RequestBody ReactionWebRequest webRequest) {

        ReactionTarget target = new ReactionTarget(webRequest.talkId(), authMember.id(), webRequest.type());
        reactionModifiable.add(target);
        return ResponseEntity.accepted().build();
    }

    @DeleteMapping
    ResponseEntity<String> remove(@AuthenticationPrincipal AuthMember authMember,
                                  @RequestBody ReactionWebRequest webRequest) {
        ReactionTarget target = new ReactionTarget(webRequest.talkId(),authMember.id(), webRequest.type());
        reactionModifiable.remove(target);
        return ResponseEntity.accepted().build();
    }
}
