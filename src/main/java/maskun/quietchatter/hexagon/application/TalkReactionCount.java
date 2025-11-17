package maskun.quietchatter.hexagon.application;

import java.util.UUID;

public record TalkReactionCount(UUID talkId, long likes, long supports) {
}
