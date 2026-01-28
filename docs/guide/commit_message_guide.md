# 커밋 메시지 컨벤션

이 문서는 [AngularJS Git Commit Message Conventions](https://gist.github.com/stephenparish/9941e89d80e2bc58a153)을 기반으로 작성되었습니다.

## 커밋 메시지 형식

```
<type>(<scope>): <subject>

<body>

<footer>
```

가독성을 위해 한 줄은 100자를 넘기지 않도록 합니다.

## 1. 제목 (Subject Line)

변경 사항에 대한 간결한 설명을 포함합니다.

### `<type>` (필수)

* `feat`: 새로운 기능 추가
* `fix`: 버그 수정
* `docs`: 문서 수정
* `style`: 코드 포맷팅, 세미콜론 누락 등 (비즈니스 로직 변경 없음)
* `refactor`: 코드 리팩토링
* `test`: 테스트 코드 추가
* `chore`: 빌드 업무 수정, 패키지 매니저 설정 등

### `<scope>` (선택)

변경 사항이 적용된 위치를 명시합니다.
예: `auth`, `logging`, `Member`, `PaymentService` 등

### `<subject>` (필수)

* 명령문, 현재 시제를 사용합니다. (예: "change" O, "changed" X, "changes" X)
    * 한국어의 경우: "~변경" 또는 "~함" 등으로 간결하게 작성
* 첫 글자는 소문자로 시작합니다. (영어 작성 시)
* 끝에 마침표(.)를 찍지 않습니다.

## 2. 본문 (Body)

* 제목과 마찬가지로 명령문, 현재 시제를 사용합니다.
* **변경 이유**와 **이전 동작과의 차이점**을 설명합니다.

## 3. 바닥글 (Footer)

### Breaking Changes (주요 변경 사항)

모든 주요 변경 사항(Breaking Changes)은 바닥글에 명시해야 합니다.
변경 내용에 대한 설명, 정당성, 마이그레이션 가이드를 포함합니다.

### 이슈 참조 (Referencing Issues)

해결된 버그나 관련 이슈는 바닥글에 별도 라인으로 작성하며 `Closes` 키워드를 사용합니다.
예: `Closes #123`
