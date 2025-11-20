package maskun.quietchatter.talk.adaptor.in;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toSet;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import maskun.quietchatter.reaction.application.in.ReactionQueryable;
import maskun.quietchatter.reaction.domain.Reaction;
import maskun.quietchatter.reaction.domain.Reaction.Type;
import maskun.quietchatter.talk.application.in.TalkQueryRequest;
import maskun.quietchatter.talk.application.in.TalkQueryable;
import maskun.quietchatter.talk.domain.Talk;
import maskun.quietchatter.talk.domain.Time;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/talks")
@RestController
@RequiredArgsConstructor
class TalkQueryApi {
    private final TalkQueryable talkQueryable;
    private final ReactionQueryable reactionQueryable;

    @GetMapping(params = "bookId")
    ResponseEntity<Page<TalkResponse>> getBy(@RequestParam("bookId") UUID bookId,
                                                    @PageableDefault Pageable pageable,
                                                    @AuthenticationPrincipal UUID memberId) {
        TalkQueryRequest request = new TalkQueryRequest(bookId, pageable);
        Page<Talk> talks = talkQueryable.findBy(request);

        Map<UUID, Set<Type>> memberReactions = Optional.ofNullable(memberId)
                .map(id -> reactionQueryable.getAllBy(id, talks.getContent().stream().map(Talk::getId).toList())
                        .stream().collect(groupingBy(Reaction::getTalkId, mapping(Reaction::getType, toSet()))))
                .orElse(Map.of());

        Page<TalkResponse> page = talks.map(talk -> {
            Set<Type> types = memberReactions.getOrDefault(talk.getId(), Set.of());
            boolean didILike = types.contains(Type.LIKE);
            boolean didISupport = types.contains(Type.SUPPORT);
            return getTalkResponse(talk, didILike, didISupport);
        });

        return ResponseEntity.ok(page);
    }

    private static TalkResponse getTalkResponse(Talk talk, boolean didILike, boolean didISupport) {
        Instant hidden = Optional.ofNullable(talk.getTime()).map(Time::hidden).orElse(null);
        return new TalkResponse(
                talk.getId(),
                talk.getBookId(),
                talk.getMemberId(),
                talk.getCreatedAt(),
                hidden,
                talk.getContent().value(),
                talk.getReactionCount().like(),
                didILike,
                talk.getReactionCount().support(),
                didISupport
        );
    }
}
