# AI Agent Guide

이 문서는 AI 에이전트가 본 프로젝트를 이해하고 개발을 보조하기 위한 가이드라인입니다.

## 1. 프로젝트 개요

- **목표**: 독서 주제 SNS
- **아키텍처**: Hexagonal Architecture
- **프로젝트 역할** 해당 프로젝트는 백엔드 API 서버를 담당합니다.

## 2. 기술 스택

- **Backend**: Java 21, Spring Boot 3.5.*
- **Database**: PostgreSQL (Main), Redis (Token/Cache), Flyway (Migration)
- **Security**: Spring Security (JWT 기반)
- **Infrastructure**: AWS LightSail, Docker, GitHub Actions

## 3. 핵심 가이드 문서

작업 성격에 따라 다음 문서들을 참고하십시오.

- **[API 설계 가이드](api_design_guide.md)**: URI, 인증, 에러 처리, 버저닝 (`/v1`)
- **[아키텍처 가이드](application_architecture_guide.md)**: 헥사고날 아키텍처, 패키지 구조
- **[인프라 가이드](infra_architecture_guide.md)**: Dev/Prod 스테이징 전략, 배포 파이프라인
- **[코드 스타일 가이드](code_style_guide.md)**: Java 컨벤션, 테스트 작성 원칙
- **[요구사항 명세서](/docs/requirements_specification.md)**: 도메인 모델 및 기능 요구사항

## 4. 에이전트 작업 프로세스 (Workflow)

### A. 구현 원칙
- **테스트 우선**: 기능 구현 시 반드시 테스트 코드(Unit/Integration)를 함께 작성합니다.
- **문서화**: API 변경 시 `RestDocs` 테스트를 업데이트하여 `openapi3.json`이 최신 상태를 유지하도록 합니다.

### B. 데이터베이스 변경
- 엔티티 필드 추가/삭제 등 스키마 변경 시 반드시 **Flyway 마이그레이션 스크립트**(`src/main/resources/db/migration`)를 작성합니다.
- 기존 데이터와의 호환성을 고려하여 `IF NOT EXISTS` 구문과 기본값 설정을 활용합니다.

### C. 검증
- 작업 완료 후 `./gradlew test`를 실행하여 전체 테스트 통과 여부를 확인합니다.
- API 문서화가 필요한 경우 `./gradlew openapi3`를 실행하여 스펙 생성을 검증합니다.
