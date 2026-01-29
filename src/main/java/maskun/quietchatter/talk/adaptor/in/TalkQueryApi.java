package maskun.quietchatter.talk.adaptor.in;

import lombok.RequiredArgsConstructor;
import maskun.quietchatter.security.AuthMember;
import maskun.quietchatter.talk.application.in.TalkQueryRequest;
import maskun.quietchatter.talk.application.in.TalkQueryable;
import maskun.quietchatter.talk.domain.Talk;
import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RequestMapping("/api/v1/talks")
@RestController
@RequiredArgsConstructor
class TalkQueryApi {
    private final TalkQueryable talkQueryable;
    private final TalkResponseMapper talkResponseMapper;

    @GetMapping(params = "bookId")
    ResponseEntity<Page<TalkResponse>> getBy(@RequestParam("bookId") UUID bookId,
                                                    @PageableDefault Pageable pageable,
                                             @Nullable @AuthenticationPrincipal AuthMember authMember) {
        TalkQueryRequest request = new TalkQueryRequest(bookId, pageable);
        Page<Talk> talks = talkQueryable.findBy(request);

        if (authMember == null) {
            return ResponseEntity.ok(talkResponseMapper.mapToResponse(talks));
        }
        return ResponseEntity.ok(talkResponseMapper.mapToResponse(talks, authMember));
    }
}
