package maskun.quietchatter.talk.domain;

public class NotTalkOwnerException extends RuntimeException {
    public NotTalkOwnerException() {
        super("본인의 톡만 수정/삭제할 수 있습니다.");
    }
}
