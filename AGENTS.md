# AI Agent Guide

This document is a guideline for AI agents to understand this project and help with development.

## 1. Project Overview

- **Goal**: Create a book-themed social network (SNS for readers).
- **Architecture**: Hexagonal Architecture.
- **Role**: This project serves as the backend API server.

## 2. Tech Stack

- **Backend**: Java 21, Spring Boot 3.5.*
- **Database**: PostgreSQL (Main), Redis (Token/Cache), Flyway (Migration).
- **Security**: Spring Security (JWT-based).
- **Infrastructure**: AWS LightSail, Docker, GitHub Actions.

## 3. Main Guidelines

Please refer to the following documents based on your current task:

- **[API Implementation Guide](/docs/guide/api_guide.md)**: Rules for URI design, REST patterns, and RestDocs.
- **[Architecture Guide](/docs/guide/architecture_guide.md)**: Details on the Hexagonal Application Architecture.
- **[Infrastructure Guide](/docs/guide/infrastructure_deployment_guide.md)**: Server CI/CD, environments, and deployment info.
- **[Security Guide](/docs/guide/security_guide.md)**: Rules for JWT Authentication and CORS configuration.
- **[Code Style Guide](/docs/guide/code_style_guide.md)**: Rules for Java conventions and clean code.
- **[Commit Message Guide](/docs/guide/commit_message_guide.md)**: Rules for formatting git commit messages.
- **[Service & Policy](https://github.com/maskun2/quiet-chatter-docs)**: External repository for project requirements, history, and policies.

## 4. Agent Working Process

### A. Planning and Design Rules

- **Use Industry Standards**: Always recommend popular, proven technologies and design patterns.
- **Give One Clear Path**: Do not offer multiple options like "A or B". Instead, decide on the best single path for this project and explain your choice clearly.
- **Be Specific**: Do not give vague instructions like "fix the settings". Provide exact steps, such as "Add AppCorsProperties and inject it into SecurityConfig".
- **Decide Early**: Make technical decisions during the planning phase so the developer can review them immediately.

### B. Implementation Rules

- **Follow Standards**: Apply the latest standard practices for Java and Spring Boot. Use proven design patterns (SOLID, OOP) and write clean code.
- **Test First**: Always write test code (Unit or Integration tests) when adding new features.
- **Update Documentation**: Update `RestDocs` tests whenever an API changes. This keeps `openapi3.json` up to date automatically.
- **Never Commit JSON Specs**: Do not commit the `openapi3.json` file. It is generated automatically during deployment.

### C. Database Changes

- **Use Flyway**: When changing the database schema (adding or removing tables/columns), you must write a Flyway migration script in `src/main/resources/db/migration`.
- **Be Safe**: Use `IF NOT EXISTS` and set default values to protect existing data.

### D. Verification

- **Run Tests**: After finishing your work, run `./gradlew test` to ensure all tests pass.
- **Check API Spec**: If you modified an API, run `./gradlew openapi3` to verify the generated specification. Reminder: Do not commit the generated JSON file.
