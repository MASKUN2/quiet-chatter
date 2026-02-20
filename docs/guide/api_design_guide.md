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
- `/v{version_number}/` 형태로 버전을 명시한다.

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

- **JWT (JSON Web Token)** 기반 인증을 사용합니다.
- **인증 토큰 확인 순서**:
  1. **Cookie**: `access_token` 쿠키 (우선)
  2. **Header**: `Authorization: Bearer <token>` 헤더 (차순위, 테스트 등)
- **Refresh Token**: 보안을 위해 `refresh_token` 쿠키(HttpOnly, Secure)로만 관리합니다.
- **OAuth (Naver)**: 
  - 로그인은 `/v1/auth/login/naver`를 통해 수행하며, 미가입 시 `200 OK`와 함께 `Register Token`을 반환합니다.
  - 회원가입은 `/v1/auth/signup/naver`를 통해 수행하며, `Register Token` 검증 후 정식 토큰을 발급합니다.

```text
# Header 예시 (쿠키 사용 불가 환경)
Authorization: Bearer <access_token>
```

## 6. CORS 정책 (CORS Policy)

환경별로 허용된 Origin 리스트를 엄격하게 관리하여 보안을 강화합니다.

- **기본 정책**: `AllowCredentials: true` 설정을 통해 쿠키 기반 인증을 지원합니다.
- **환경별 허용 Origin**:
    - **Production**: `https://quiet-chatter.com`
    - **Development**: `https://dev.quiet-chatter.com`, `http://localhost:5173`
    - **Local**: `http://localhost:5173`, `http://127.0.0.1:5173`

## 7. API 문서화 (API Documentation)

### 7.1 문서화 전략

- **Spring Rest Docs**와 **restdocs-api-spec** 라이브러리를 사용하여 테스트 기반으로 문서를 자동 생성한다.
- 테스트가 통과하지 않으면 문서가 생성되지 않으므로, 문서의 정확성(신뢰성)을 보장한다.
- 최종 결과물은 **OpenAPI 3.0 (Swagger) JSON** 포맷으로 제공한다.

### 7.2 문서 생성 및 배포 흐름 (Automated Flow)

1. **테스트 작성**: 각 API 엔드포인트에 대해 `RestDocs`를 적용한 테스트 코드(`@Tag("restdocs")`)를 작성한다.
2. **자동 생성 및 패키징**: 빌드 시(`bootJar` 태스크) 다음 과정이 자동으로 수행된다.
    - `testDocs` 태스크 실행 (문서용 테스트만 선별 실행)
    - `openapi3` 태스크 실행 (`build/api-spec/openapi3.json` 생성)
    - 생성된 JSON 파일을 JAR 내부의 `static/docs/` 경로로 자동 포함
3. **CI/CD 및 서버 배포**: GitHub Actions에서 빌드 시 API 명세서가 포함된 JAR를 도커 이미지로 만들어 배포하며, 서버의 `Watchtower`가 이를 감지하여 자동 갱신한다.
4. **수동 커밋 금지**: `openapi3.json` 파일은 빌드 시점에 동적으로 생성되므로 **코드 저장소(Git)에 직접 커밋하지 않는다.** 개발자는 오직 **테스트 코드의 정확성**에만 집중한다.

### 7.3 문서 조회 (Accessing Spec)

- **배포 환경**: `GET /v1/spec` 엔드포인트를 통해 서버에 내장된 최신 JSON 명세서를 조회할 수 있다.
- **로컬 개발 시**: `./gradlew openapi3` 실행 후 동일한 엔드포인트(`localhost:8080/v1/spec`)에서 `build` 디렉토리의 파일을 실시간으로 확인할 수 있다.

### 7.3 필수 포함 정보

테스트 코드 작성 시 다음 정보를 반드시 문서화해야 한다.

- 요청/응답 필드 (Field Descriptor)
- 경로 변수 (Path Parameters)
- 쿼리 파라미터 (Query Parameters)
- 필수 여부 및 설명