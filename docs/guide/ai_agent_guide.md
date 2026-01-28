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

## 3. 작업 가이드

작업시 다음 가이드 문서를 참고합니다.

- [requirements_specification.md](/docs/requirements_specification.md)
- [application_architecture_guide.md](/docs/guide/application_architecture_guide.md)
- [project_history.md](/docs/project_history.md)
- [code_style_guide.md](/docs/guide/code_style_guide.md)
- [commit_message_guide.md](/docs/guide/commit_message_guide.md)
- [infra_architecture_guide.md](/docs/guide/infra_architecture_guide.md)

## 4. 에이전트 수행 지침
- 테스트를 작성합니다.
- 엔티티 변경 등 DB 스키마에 변화가 생길 경우, 반드시 Flyway 마이그레이션 스크립트(`src/main/resources/db/migration`)를 작성합니다.
- 마이그레이션 스크립트 작성 시 `IF NOT EXISTS` 구문을 사용하여 기존 DB와의 충돌을 방지합니다.
- 수정시 이미 작성된 테스트를 확인한다.
