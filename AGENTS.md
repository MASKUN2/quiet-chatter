# AI Agent Guide (AGENTS.md)

이 문서는 AI 에이전트가 `Quiet Chatter` 프로젝트를 이해하고 개발을 보조하기 위한 가이드라인입니다.

## 1. 프로젝트 개요

- **목표**: 수줍은 사람들을 위한 독서 SNS (익명성 강조)
- **핵심 가치**: 로그인 없는 접근성, 조용한 리액션, 자동 게시글 숨김
- **아키텍처**: 헥사고날 아키텍처 (Hexagonal Architecture)

## 2. 기술 스택

- **Backend**: Java 21, Spring Boot 3.x
- **Database**: PostgreSQL (Main), Redis (Token/Cache)
- **Security**: Spring Security (JWT 기반, 익명 사용자 자동 게스트 승급)
- **Frontend**: Thymeleaf, Vanilla JS, CSS
- **Infrastructure**: AWS LightSail, Docker, GitHub Actions

## 3. 아키텍처 및 패키지 구조

프로젝트는 도메인 중심의 헥사고날 아키텍처를 따릅니다.

- `domain`: 순수 비즈니스 로직 및 엔티티 (외부 의존성 없음)
- `application`: 유스케이스(Service) 및 포트(Port) 인터페이스
- `adaptor.in`: Web API, Controller (외부 -> 내부)
- `adaptor.out`: DB Repository 구현체, 외부 API 클라이언트 (내부 -> 외부)

## 4. 코딩 컨벤션 및 원칙

- **도메인 모델**: 원시값은 VO(Value Object)로 감싸고, 데이터를 꺼내기보다 객체에 메시지를 던지는 방식(Tell, Don't Ask)을 지향합니다.
- **메서드**: 한 가지 기능만 수행하며, 가급적 15라인 이내로 유지합니다.
- **테스트**: 스프링 컨텍스트 없는 단위 테스트를 선호하며, 외부 의존성은 모킹합니다.
- **불변성**: 가능한 한 불변 객체를 사용합니다.

## 5. 주요 비즈니스 메커니즘

### 익명 사용자 자동 게스트 승급

- 모든 사용자는 로그인 없이 활동 가능합니다.
- `AnonymousToGuestPromotionFilter`를 통해 쓰기 권한이 필요한 요청 시 자동으로 게스트 계정을 생성하고 JWT를 발급합니다.
- 상세 내용은 `docs/feature/non-login-access.md`를 참조하세요.

## 6. 에이전트 수행 지침

- 새로운 기능을 추가할 때 헥사고날 아키텍처의 계층 구조를 엄격히 준수하세요.
- 기존의 VO 기반 도메인 설계 방식을 따르세요.
- 변경 사항이 발생하면 관련 문서(`README.md`, `docs/feature/*.md`)를 함께 업데이트하세요.
