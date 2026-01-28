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

## 3. 아키텍처 및 패키지 구조

프로젝트는 도메인 중심의 헥사고날 아키텍처를 따릅니다.

- `domain`: 순수 비즈니스 로직 및 엔티티 (외부 의존성 없음)
- `application`: 유스케이스(Service) 및 포트(Port) 인터페이스
- `adaptor.in`: Web API, Controller (외부 -> 내부)
- `adaptor.out`: DB Repository 구현체, 외부 API 클라이언트 (내부 -> 외부)

## 4. 컨벤션 및 원칙

작업시 다음 문서를 참고해야합니다.

- [code_style_guide.md](/docs/guide/code_style_guide.md)
- [commit_message_guide.md](/docs/guide/commit_message_guide.md)

## 6. 에이전트 수행 지침

- 새로운 기능을 추가할 때 헥사고날 아키텍처의 계층 구조를 엄격히 준수하세요.
- 기존의 VO 기반 도메인 설계 방식을 따르세요.
- 변경 사항이 발생하면 관련 문서(`README.md`, `docs/feature/*.md`)를 함께 업데이트하세요.
- 테스트를 작성합니다.
