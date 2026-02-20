# 인프라스트럭처 아키텍쳐

```mermaid
    C4Context
    title Quiet Chatter : 인프라스트럭처 아키텍쳐
    Person(user, "사용자")

    Boundary(system, "클라우드 서버", "AWS Light Sail") {
        Container(ws, "웹서버", "Nginx", "클라이언트 페이지 서빙 / 라우팅")
        Container(api_server, "API 서버", "Spring boot", "Flyway를 통한 DB 마이그레이션 포함")
        Container(db, "관계형 데이터 베이스", "PostgreSQL")
        Container(in_memory_db, "인 메모리 데이터 베이스", "Redis", "캐시/인증토큰관리")
    }

    Boundary(git, "VCS", "GitHub") {
        Component(gitAction, "CI/CD", "Git Hub Action")
        ComponentDb(repository, "프로젝트 저장소", "Git Hub Repository")
        Person(developer, "개발자")
    }
    System(dockerhub, "이미지 저장소", "Docker Hub")
    BiRel(user, ws, "")
    Rel(gitAction, dockerhub, "Docker 이미지 푸시")
    BiRel(ws, api_server, "")
    BiRel(in_memory_db, api_server, "")
```
## 스테이징 전략 (Staging Strategy)

### Production (운영)
- **도메인 (API)**: `api.quiet-chatter.com`
- **도메인 (Frontend)**: `quiet-chatter.com`
- **Docker 이미지**: `maskun2/quiet-chatter:latest`
- **데이터베이스**: PostgreSQL (`quiet_chatter`), Redis (DB 0)
- **배포**: `main` 브랜치 푸시 시 GitHub Actions를 통해 자동 배포

### Development (개발)
- **도메인 (API)**: `dev-api.quiet-chatter.com`
- **도메인 (Frontend)**: `dev.quiet-chatter.com`
- **Docker 이미지**: `maskun2/quiet-chatter-dev:latest`
- **데이터베이스**: PostgreSQL (`quiet_chatter_dev`), Redis (DB 1)
- **배포**: `dev` 브랜치 푸시 시 GitHub Actions를 통해 자동 배포

## 배포 파이프라인 (CI/CD)
1. GitHub 푸시 감지 (`main` 또는 `dev`)
2. Gradle 빌드 및 테스트 수행
3. Docker 이미지 빌드 및 Docker Hub 푸시
4. 서버(AWS Lightsail)의 Watchtower가 새 이미지를 감지하여 컨테이너 재시작
