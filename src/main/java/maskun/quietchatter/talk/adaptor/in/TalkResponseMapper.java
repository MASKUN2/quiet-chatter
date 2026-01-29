package maskun.quietchatter.talk.adaptor.in;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toSet;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import maskun.quietchatter.reaction.application.in.ReactionQueryable;
import maskun.quietchatter.reaction.domain.Reaction;
import maskun.quietchatter.reaction.domain.Reaction.Type;
import maskun.quietchatter.security.AuthMember;
import maskun.quietchatter.talk.domain.Talk;
import org.jspecify.annotations.NullMarked;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@NullMarked
@Component
@RequiredArgsConstructor
public class TalkResponseMapper {
    private final ReactionQueryable reactionQueryable;

    Page<TalkResponse> mapToResponse(Page<Talk> talks) {
        return talks.map(TalkResponseMapper::mapToTalkResponse);
    }

    Page<TalkResponse> mapToResponse(Page<Talk> talks, AuthMember authMember) {
        Page<TalkResponse> talkResponses = talks.map(TalkResponseMapper::mapToTalkResponse);

        List<UUID> talkIds = talkResponses.getContent().stream().map(TalkResponse::id).toList();

        Map<UUID, Set<Type>> talkReactionsMap = reactionQueryable.getAllBy(authMember.id(), talkIds)
                .stream().collect(groupingBy(Reaction::getTalkId, mapping(Reaction::getType, toSet())));

        return talkResponses.map(updateReactionDid(talkReactionsMap));
    }

    private static TalkResponse mapToTalkResponse(Talk talk) {
        return new TalkResponse(
                talk.getId(),
                talk.getBookId(),
                talk.getMemberId(),
                talk.getCreatedAt(),
                talk.getDateToHidden(),
                talk.getContent(),
                talk.getLikeCount(),
                false,
                talk.getSupportCount(),
                false,
                talk.isModified()
        );
    }

    private Function<? super TalkResponse, TalkResponse> updateReactionDid(
            final Map<UUID, Set<Type>> talkReactionsMap) {
        return resp -> {
            UUID talkId = resp.id();
            if (!talkReactionsMap.containsKey(talkId)) {
                return resp;
            }
            Set<Type> didReactions = talkReactionsMap.getOrDefault(talkId, Set.of());
            boolean didILike = didReactions.contains(Type.LIKE);
            boolean didISupport = didReactions.contains(Type.SUPPORT);
            return buildTalkResponseWithLikesSupport(resp, didILike, didISupport);
        };
    }

    private static TalkResponse buildTalkResponseWithLikesSupport(TalkResponse resp, boolean didILike,
                                                                  boolean didISupport) {
        return new TalkResponse(
                resp.id(),
                resp.bookId(),
                resp.memberId(),
                resp.createdAt(),
                resp.dateToHidden(),
                resp.content(),
                resp.like_count(),
                didILike,
                resp.support_count(),
                didISupport,
                resp.isModified()
        );
    }
}
