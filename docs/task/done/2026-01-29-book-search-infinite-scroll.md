# Agent Working Procedure
1. Review the code base and prepare for work based on the "## Developer Requirements" section below.
2. If the last section is a developer section like "## Developer Requirements Update" or "## Developer Review Response", start from that part.
3. If you need a decision from the developer, add a section called "## Agent Review Request" at the end of this file and stop working.
4. When work is finished, add a section called "## Agent Work Execution" and describe what you did.

## Developer Requirements
- Focus on the `book` domain package.
- The current search uses pages. Change it to offset-based infinite scroll.
- Use the `Slice` type instead of `Page`.
- Filter out books without an ISBN when fetching from external APIs.
- To reduce coupling, define a new VO `ExternalBook` for external API results instead of using the `Book` entity.
- Update the existing API controller handler.
- Stop allowing a null ISBN in the `Book` entity.

## Agent Work Execution: 2026-01-29 15:05
- **Domain Model Change**: Made `isbn` mandatory in the `Book` entity and added validation logic to `newOf`.
- **Added VO**: Added the `ExternalBook` record for external API responses to lower coupling with the domain entity.
- **Infinite Scroll Implementation**:
    - Updated `ExternalBookSearcher` and `NaverBookSearcher` to return `Slice<ExternalBook>`.
    - Added logic to filter out items without an ISBN from Naver API responses.
    - Updated `BookQueryable` and `BookQueryService` to return `Slice<Book>`.
    - Updated the `BookApi` search endpoint to return `Slice<BookResponse>`.
- **Test Code Updates**:
    - Refactored `BookQueryServiceTest`, `BookApiTest`, `NaverBookSearcherTest`, and `NaverBookSearcherApiTest` for `Slice` and `ExternalBook`.
    - Verified all related tests passed.
