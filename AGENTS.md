# AI Agent Guide - legacy-quiet-chatter

이 문서는 AI 에이전트가 레거시 프로젝트를 이해하고 개발을 돕기 위한 지침입니다.

## 1. 프로젝트 개요

- 목표: 독서 나눔 소셜 네트워크 서비스 (QuietChatter) 구축.
- 아키텍처: 헥사고날 아키텍처 (Hexagonal Architecture).
- 역할: 백엔드 API 서버.

## 2. 기술 스택

- 백엔드: Java 21, Spring Boot 3.5.x.
- 데이터베이스: PostgreSQL, Redis, Flyway.
- 보안: Spring Security (JWT).
- 인프라: AWS LightSail, Docker, GitHub Actions.

## 3. 주요 가이드라인

작업 수행 시 다음 문서를 참조하십시오:

- API 구현 가이드 (docs/guide/api_guide.md): URI 설계, REST 패턴, RestDocs 규칙.
- 아키텍처 가이드 (docs/guide/architecture_guide.md): 헥사고날 아키텍처 세부 사항.
- 인프라 가이드 (docs/guide/infrastructure_deployment_guide.md): CI/CD, 환경 설정 및 배포 정보.
- 보안 가이드 (docs/guide/security_guide.md): JWT 인증 및 CORS 설정.
- 코드 스타일 가이드 (docs/guide/code_style_guide.md): Java 컨벤션 및 클린 코드 규칙.
- 커밋 메시지 가이드 (docs/guide/commit_message_guide.md): Git 커밋 메시지 형식.
- 제품 요구사항 (docs/requirements/prd.md): 서비스 목적, 기능 및 정책.

## 4. 에이전트 작업 지침

모든 작업 시작 전 및 작업 중에 superpowers 스킬 목록을 항상 확인하고 상황에 맞는 스킬을 활성화하여 사용하십시오.

### A. 공통 원칙
- 모든 서비스는 헥사고날 아키텍처를 따르며, 어댑터 패키지 명칭은 adaptor로 통일합니다.
- 마크다운 작성 시 강조 서식(bold, italics)과 이모티콘 사용을 절대 금지합니다.
- 업계 표준 우선: 대중적이고 검증된 기술 및 패턴을 제안하십시오.
- 단일 경로 제안: 상황에 최적화된 하나의 방안을 선택하고 설명하십시오.

### B. 구현 규칙

- 최신 표준 준수: Java 및 Spring Boot의 최신 표준 관행을 적용하십시오 (SOLID, OOP).
- 테스트 우선: 기능 추가 시 반드시 테스트 코드(단위 또는 통합 테스트)를 작성하십시오.
- 문서 최적화: API 변경 시 RestDocs 테스트를 업데이트하여 명세를 유지하십시오.

### C. 데이터베이스 변경

- Flyway 사용: 스키마 변경 시 src/main/resources/db/migration에 마이그레이션 스크립트를 작성하십시오.
- 안정성 확보: IF NOT EXISTS 구문과 기본값을 사용하여 기존 데이터를 보호하십시오.

### D. 검증 절차

- 테스트 실행: 작업 완료 후 ./gradlew test를 실행하여 모든 테스트 통과를 확인하십시오.
- API 스펙 확인: API 수정 시 ./gradlew openapi3를 실행하여 명세를 검증하십시오 (생성된 JSON은 커밋 금지).
