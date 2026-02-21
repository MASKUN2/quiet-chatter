# Infrastructure Architecture

```mermaid
    C4Context
    title Quiet Chatter : Infrastructure Architecture
    Person(user, "User")

    Boundary(system, "Cloud Server", "AWS Light Sail") {
        Container(ws, "Web Server", "Nginx", "Serving client pages / Routing")
        Container(api_server, "API Server", "Spring boot", "Includes DB migration via Flyway")
        Container(db, "Relational Database", "PostgreSQL")
        Container(in_memory_db, "In-memory Database", "Redis", "Cache/Auth Token management")
    }

    Boundary(git, "VCS", "GitHub") {
        Component(gitAction, "CI/CD", "GitHub Actions")
        ComponentDb(repository, "Project Repository", "GitHub Repository")
        Person(developer, "Developer")
    }
    System(dockerhub, "Image Registry", "Docker Hub")
    BiRel(user, ws, "")
    Rel(gitAction, dockerhub, "Push Docker Image")
    BiRel(ws, api_server, "")
    BiRel(in_memory_db, api_server, "")
```
## Staging Strategy

### Production
- **Domain (API)**: `api.quiet-chatter.com`
- **Domain (Frontend)**: `quiet-chatter.com`
- **Docker Image**: `maskun2/quiet-chatter:latest`
- **Database**: PostgreSQL, Redis (DB 0)
- **Release Trigger**: `Semantic Release` runs when pushing/merging to `main` (updates version, creates tags, manages CHANGELOG).
- **Deployment Trigger**: Auto-deploy when pushing/merging to `prod` (detected by Watchtower).

### Development
- **Domain (API)**: `dev-api.quiet-chatter.com`
- **Domain (Frontend)**: `dev.quiet-chatter.com`
- **Docker Image**: `maskun2/quiet-chatter-dev:latest`
- **Database**: PostgreSQL, Redis (DB 1)
- **Deployment Trigger**: Auto-deploy when pushing to `dev` (detected by Watchtower).

## Deployment & Release Pipeline (CI/CD)

### 1. Development Server
1. Push to `dev` branch is detected.
2. Gradle build and tests run (including `testDocs`).
3. Build `quiet-chatter-dev` Docker image and push to Docker Hub.
4. **Watchtower** detects the new image and restarts the dev server container automatically.

### 2. Release & Production Server
1. **Release Management**: `release.yml` runs when merging to `main`.
    - Analyzes commit messages to decide the next version.
    - Updates version in `build.gradle` and creates `CHANGELOG.md`.
    - Issues a Git tag and creates a GitHub Release.
2. **Production Deployment**: `prod-deploy.yml` runs when pushing/merging to `prod`.
    - Gradle build (skips tests) and auto-generates `openapi3.json`.
    - Build `quiet-chatter` Docker image and push to Docker Hub.
3. **Watchtower** detects the new image on the production server and restarts the container.

---

## Infrastructure Architecture Diagram (Staging Flow)

```mermaid
graph TD
    subgraph "Local Development"
        D[Developer] -- "Push Feature" --> GH_F[Feature Branch]
    end

    subgraph "GitHub Actions (CI/CD)"
        GH_F -- "PR to dev" --> DEV[dev branch]
        DEV -- "Auto Deploy (dev-deploy.yml)" --> DOCKER_DEV[Docker Hub: quiet-chatter-dev]
        DEV -- "PR to main" --> MAIN[main branch]
        
        MAIN -- "Push to main" --> SR[release.yml: Semantic Release]
        SR -- "Update Version & Tag" --> MAIN
        MAIN -- "Merge to prod" --> PROD[prod branch]
        PROD -- "Auto Deploy (prod-deploy.yml)" --> DOCKER_PROD[Docker Hub: quiet-chatter]
    end

    subgraph "AWS LightSail (Deployment)"
        DOCKER_DEV -- "Watchtower" --> CONT_DEV[Container: app-dev]
        DOCKER_PROD -- "Watchtower" --> CONT_PROD[Container: app]
    end

    CONT_DEV --- DB_DEV[(PostgreSQL: dev)]
    CONT_PROD --- DB_PROD[(PostgreSQL: prod)]
```
