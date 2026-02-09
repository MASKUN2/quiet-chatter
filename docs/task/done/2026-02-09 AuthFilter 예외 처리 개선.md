# AuthFilter 예외 처리 개선

## 작업 내용
- `AuthFilter`에서 리프레시 토큰 처리 중 `AuthTokenException`, `NoSuchElementException`, `AuthMemberNotFoundException` 발생 시 500 에러 대신 익명 사용자로 처리하도록 개선
- `AuthFilterTest`에 관련 테스트 케이스 추가 및 기존 테스트 케이스 수정

## 변경 파일
- `src/main/java/maskun/quietchatter/security/adaptor/AuthFilter.java`
- `src/test/java/maskun/quietchatter/security/adaptor/AuthFilterTest.java`
