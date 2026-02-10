# Refactor Talk Domain Logic

## 작업 내용
- `Talk` 도메인 엔티티 내 `validateOwner` 메서드 추가 및 소유권 검증 로직 이동
- `Talk` 생성자에서 `dateToHidden` null 처리 로직을 엔티티 내부로 이동 (기본값: 12개월 후)
- `NotTalkOwnerException` 추가 및 `WebExceptionHandler`에서 403 Forbidden 처리
- `TalkCommandService` 리팩토링: `create` 메서드 간소화 및 `validateOwner` 메서드 제거

## 관련 이슈
- 개선 작업

## 테스트
- `TalkCommandServiceTest`: `validateOwner` 예외 테스트 및 `create` 기본값 테스트 추가
- `WebExceptionHandlerTest`: `NotTalkOwnerException` 핸들링 테스트 추가
- 전체 테스트 수행 (일부 타임아웃 발생했으나 관련 유닛 테스트 통과 확인)
