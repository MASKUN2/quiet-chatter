package maskun.quietchatter.book.application.out;

import maskun.quietchatter.book.application.in.Keyword;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface ExternalBookSearcher {
    Slice<ExternalBook> findByKeyword(Keyword keyword, Pageable pageRequest);
}
