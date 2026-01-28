# AI Agent Guide

이 문서는 AI 에이전트가 본 프로젝트를 이해하고 개발을 보조하기 위한 가이드라인입니다.

## 1. 프로젝트 개요

- **목표**: 독서 주제 SNS
- **아키텍처**: Hexagonal Architecture
- **프로젝트 역할** 해당 프로젝트는 백엔드 API 서버를 담당합니다.

## 2. 기술 스택

- **Backend**: Java 21, Spring Boot 3.5.*
- **Database**: PostgreSQL (Main), Redis (Token/Cache)
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
