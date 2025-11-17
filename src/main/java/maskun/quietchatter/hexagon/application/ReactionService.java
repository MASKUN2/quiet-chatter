package maskun.quietchatter.hexagon.application;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import maskun.quietchatter.hexagon.domain.reaction.Reaction.Type;
import maskun.quietchatter.hexagon.inbound.TalkReactable;
import maskun.quietchatter.hexagon.outbound.MemberRepository;
import maskun.quietchatter.hexagon.outbound.ReactionManipulator;
import maskun.quietchatter.hexagon.outbound.TalkRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReactionService implements TalkReactable {
    private final ReactionManipulator reactionManipulator;
    private final MemberRepository memberRepository;
    private final TalkRepository talkRepository;

    @Override
    public void add(UUID talkId, UUID memberId, Type type) {
        talkRepository.require(talkId);
        memberRepository.require(memberId);

        reactionManipulator.add(talkId, memberId, type);
    }

    @Override
    public void remove(UUID talkId, UUID memberId, Type type) {
        talkRepository.require(talkId);
        memberRepository.require(memberId);

        reactionManipulator.remove(talkId, memberId, type);
    }
}
