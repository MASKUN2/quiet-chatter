# AuthFilter 안정성 개선 및 오타 수정

## 개발자 요구사항
- AuthFilter에서 리프레시 토큰 처리 중 예외 발생 시 500 에러가 아닌 익명 사용자로 처리하도록 개선.
- AppCookieProperties 오타 수정.

## 에이전트 작업수행내용 : 2026-02-04
1. **`AuthFilter.java` 개선**:
   - `tryWithRefreshToken` 메소드 내에서 `AuthTokenException`, `NoSuchElementException`, `AuthMemberNotFoundException`을 catch하여 `null`을 반환하도록 수정.
   - 이를 통해 잘못된 리프레시 토큰이나 삭제된 회원 정보로 인한 500 에러 방지.

2. **`AppCookieProperties.java` 수정**:
   - 예외 메시지 오타 수정 ("must not be or blank" -> "must not be blank").

3. **테스트 코드 작성**:
   - `AuthFilterTest.java`: `ExpiredAccessTokenAndExpiredRefreshTokenThenAnonymous` 및 `RefreshTokenNotFoundThenAnonymous` 테스트 케이스 추가/수정하여 개선된 로직 검증.
