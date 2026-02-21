# AI Agent Guide

This document is a guideline for AI agents to understand this project and help with development.

## 1. Project Overview

- **Goal**: SNS for readers (book-themed social network).
- **Architecture**: Hexagonal Architecture.
- **Role**: This project is the backend API server.

## 2. Tech Stack

- **Backend**: Java 21, Spring Boot 3.5.*
- **Database**: PostgreSQL (Main), Redis (Token/Cache), Flyway (Migration).
- **Security**: Spring Security (JWT-based).
- **Infrastructure**: AWS LightSail, Docker, GitHub Actions.

## 3. Main Guidelines

Please check these documents depending on your task:

- **[API Design Guide](api_design_guide.md)**: URI, authentication, error handling, versioning (`/v1`).
- **[Architecture Guide](application_architecture_guide.md)**: Hexagonal architecture, package structure.
- **[Infra Guide](infra_architecture_guide.md)**: Dev/Prod strategies, deployment pipeline.
- **[Code Style Guide](code_style_guide.md)**: Java conventions, testing rules.
- **[Requirements Specification](/docs/requirements_specification.md)**: Domain model and features.

## 4. Agent Working Process (Workflow)

### A. Planning and Design Rules
- **Use Industry Standards**: Recommend popular technologies and designs that many people use.
- **Give One Clear Path**: Do not give multiple choices like "A or B". Decide on the **best single path** for this project and explain it clearly.
- **Be Specific**: Don't say "fix settings". Instead, say something like "Add AppCorsProperties and inject it into SecurityConfig".
- **Decide Early**: Make technical decisions during the planning stage so the developer can review them right away.

### B. Implementation Rules

- **Follow Standards**: Use the latest standard practices in the Java/Spring Boot ecosystem. Use proven design patterns (SOLID, OOP) and clean code.
- **Test First**: Always write test code (Unit/Integration) when adding features.
- **Documentation**: Update `RestDocs` tests when API changes. This keeps `openapi3.json` up to date. **Do not commit the JSON file itself.**

### C. Database Changes
- When changing the database schema (adding/removing fields), you must write a **Flyway migration script** (`src/main/resources/db/migration`).
- Use `IF NOT EXISTS` and set default values to keep existing data safe.

### D. Verification
- After finishing work, run `./gradlew test` to check if everything works.
- If you change an API, run `./gradlew openapi3` to check the spec. **Do not commit the generated JSON file.** (It is created automatically during deployment.)
