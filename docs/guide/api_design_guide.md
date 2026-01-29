# API Design Guide

이 문서는 프로젝트의 RESTful API 설계를 위한 공통 가이드라인을 정의합니다.
일관성 있는 인터페이스를 제공하여 API 사용성을 높이고 유지보수를 용이하게 하는 것을 목적으로 합니다.

## 1. URI 설계 (URI Design)

### 1.1 기본 구조

- 모든 API는 **소문자**를 사용한다.
- 단어의 구분은 **하이픈(-, kebab-case)**을 사용한다.
- 자원(Resource)은 **복수형 명사**를 사용한다.
- URL의 마지막에 슬래시(/)를 포함하지 않는다.

### 1.2 버저닝 (Versioning)

- API의 하위 호환성을 보장하기 위해 **URI Path Versioning** 전략을 사용한다.
- `/api/v{version_number}/` 형태로 버전을 명시한다.

## 2. HTTP 메서드 (HTTP Methods)

자원에 대한 행위는 적절한 HTTP 메서드로 표현한다.

- **GET** (조회): 리소스의 상태를 조회한다. (멱등성 O)
- **POST** (생성): 새로운 리소스를 생성하거나, 컨트롤러 형태의 명령을 실행한다. (멱등성 X)
- **PUT** (수정): 리소스 전체를 수정한다. (멱등성 O). 본 프로젝트에서는 PUT을 사용한 리소스 생성은 지양한다.
- **DELETE** (삭제): 리소스를 삭제한다. (멱등성 O)
- ~~PATCH~~ : 본 프로젝트에서는 지양한다.

## 3. 요청 및 응답 (Request & Response)

### 3.1 포맷

- 요청과 응답 본문(Body)은 **JSON** 포맷을 기본으로 한다.
- `Content-Type: application/json`

### 3.2 네이밍 컨벤션 (JSON)

- JSON 필드명은 **CamelCase (lowerCamelCase)**를 기본 원칙으로 한다.
    - *참고: Java 객체의 필드명과 1:1 매핑을 유지하여 변환 오버헤드를 줄인다.*
- 날짜/시간 필드는 ISO-8601 표준 문자열 형식을 따른다.

### 3.3 성공 응답

- **리소스 반환**: 상태 코드 `200 OK`와 함께 리소스 객체를 반환한다.
- **생성 성공**: 상태 코드 `201 Created`와 함께 생성된 리소스의 식별자 등을 반환할 수 있다.
- **비동기/작업 수락**: 즉각적인 처리가 완료되지 않았거나 반환할 내용이 없는 경우 `202 Accepted`를 사용한다. (Body 없음)
- **내용 없음**: 삭제나 수정 후 반환할 내용이 명백히 없는 경우 `204 No Content`를 사용한다. (Body 없음)

### 3.4 페이지네이션 (Pagination)

- Spring Data의 `Pageable` 인터페이스 규격을 따른다.
- 쿼리 파라미터:
    - `page`: 페이지 번호 (0부터 시작)
    - `size`: 페이지 당 항목 수
    - `sort`: 정렬 기준 (예: `createdAt,desc`)
- 응답 구조: `content` 배열과 `page` 메타데이터를 포함한다.

## 4. 에러 처리 (Error Handling)

### 4.1 포맷 (RFC 7807)

- 에러 응답은 Spring MVC의 `ProblemDetail` 객체(RFC 7807)를 사용한다.
- 일관된 에러 형식을 통해 클라이언트가 에러 원인을 명확히 파악할 수 있도록 한다.

### 4.2 주요 필드

- **type**: 에러 유형을 식별하는 URI (기본값: "about:blank")
- **title**: 에러 유형에 대한 간략한 설명
- **status**: HTTP 상태 코드
- **detail**: 에러에 대한 구체적인 설명
- **instance**: 에러가 발생한 리소스의 URI

### 4.4 주요 에러 상태 코드

- **400 Bad Request**: 클라이언트의 요청이 잘못됨 (유효성 검증 실패 등).
- **401 Unauthorized**: 인증이 필요함.
- **403 Forbidden**: 접근 권한이 없음.
- **404 Not Found**: 리소스를 찾을 수 없음.
- **500 Internal Server Error**: 서버 내부 오류.

## 5. 인증 (Authentication)

- JWT (JSON Web Token) 기반 인증을 사용한다.
- **액세스 토큰(Access Token)**은 다음 순서로 확인한다.
  1. **Cookie**: `access_token` 쿠키
  2. **Header**: `Authorization: Bearer <token>` 헤더
- **리프레시 토큰(Refresh Token)**은 보안을 위해 **Cookie** (`refresh_token`)로만 전달한다 (HttpOnly).

```text
# Header 예시
Authorization: Bearer <access_token>
```
