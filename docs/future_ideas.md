# Future Ideas & Improvements

이 문서는 프로젝트의 미래 발전 방향, 기술적 개선 아이디어, 그리고 효율적인 협업 프로세스에 대한 제안을 기록하는 공간입니다.

## 1. API 명세 기반의 프론트엔드 협업 자동화

현재 백엔드에서는 `Spring Rest Docs` + `restdocs-api-spec`을 통해 테스트 기반의 신뢰성 있는 `OpenAPI 3.0 (JSON)` 명세서를 `/api/v1/spec` 엔드포인트로
제공하고 있습니다.
이를 프론트엔드 개발 프로세스와 결합하여 생산성과 안정성을 극대화하는 방안을 제안합니다.

### 1.1 타입 및 코드 자동 생성 (Code Generation)

API 명세서를 보고 수동으로 타입을 정의하는 것이 아니라, 자동 생성 도구를 도입하여 "백엔드 변경 -> 프론트엔드 컴파일 에러 -> 즉시 수정"의 사이클을 구축합니다.

* **도구 추천:**
    * **openapi-typescript:** JSON 스펙을 읽어 TypeScript `interface`를 자동으로 생성합니다. 가장 가볍고 유연하여 기존 프로젝트에 도입하기 쉽습니다.
    * **Orval / TanStack Query (React Query):** 타입뿐만 아니라 데이터 페칭을 위한 `Hook` 코드(`useQuery`, `useMutation`)까지 생성해줍니다.

* **예상 워크플로우:**
    1. 프론트엔드 개발자가 로컬에서 `npm run api-sync` 실행.
    2. 스크립트가 `http://api-server/api/v1/spec`에서 최신 JSON을 다운로드.
    3. `openapi-typescript`가 `src/types/api.d.ts` 파일을 갱신.
    4. 변경된 타입(예: `Boolean` -> `boolean`)으로 인해 코드에서 컴파일 에러 발생.
    5. 개발자가 이를 인지하고 즉시 수정.

### 1.2 시각화 도구 활용 (Documentation UI)

JSON 파일은 사람이 읽기 어려우므로, 이를 시각화하여 보여주는 도구를 활용합니다.

* **Swagger UI / Redoc:**
    * 프론트엔드 로컬 환경이나 별도의 문서 서버에서 Swagger UI를 띄우고, 백엔드의 `/api/v1/spec` URL을 입력하여 문서를 열람합니다.
    * 백엔드 서버에 `springdoc-openapi-ui`를 내장하는 것보다, 프론트엔드나 인프라 레벨에서 별도로 띄우는 것이 백엔드를 가볍게 유지하는 데 유리합니다.

### 1.3 API 변경 감지 및 알림 (Breaking Change Detection)

백엔드 배포 시, API에 하위 호환성을 깨뜨리는 변경(Breaking Change)이 있는지 자동으로 검사합니다.

* **OpenAPI Diff:**
    * CI 파이프라인에서 "이전 버전의 스펙"과 "현재 빌드된 스펙"을 비교합니다.
    * **Breaking Change 감지 시:**
        * PR(Pull Request)에 경고 코멘트 자동 작성.
        * 슬랙/디스코드 등 메신저로 "🚨 API 변경됨: `didLike` 필드 타입 변경" 알림 전송.
    * 도구: `oasdiff`, `openapi-diff`

---
