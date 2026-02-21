# Project Development History

This document records the development flow, major technical decisions, and trade-offs from the early stages of the project to the present.

## How to Write

- Write in chronological order.
- use `##` for a section covering about a month. Format: `## Title of the Period (yyyy-MM)`
- Use `###` for a specific task. Format: `### Task Title : yyyy-MM-dd`
- Use descriptive prose without unnecessary decoration.
- Explain problems, conflicts, trade-offs, and reasons for technical choices.
- Explain the structure and principles without mentioning specific class names from this project. You can mention standard JDK or Spring types.

---

## Woowa Tech Course Open Mission (2025-11)

"Quiet Chatter: You Belong Here"
This project started as an anonymous book review social network for introverted readers. The main goal was to apply OOP principles, TDD, and Clean Code learned during the 3-week precourse to a real product.

### Initial Infrastructure Setup : 2025-11-08

I chose familiar technology like Java 21, Spring Boot, PostgreSQL, and Thymeleaf to build a quality prototype quickly. While React is good for user experience, I chose Thymeleaf (SSR) to focus on backend logic and keep the UI simple. For hosting, I used AWS LightSail because it is cost-effective and easy to manage. I built a CI/CD pipeline with GitHub Actions and Docker Compose. For the domain model, I used `UUID` instead of `Long` Auto Increment to prepare for distributed environments. I used TestContainers to run PostgreSQL for tests to match the production environment.

### External Integration and Test Improvement : 2025-11-12

I improved my testing strategy to handle the Naver Book Search API. I separated external API tests using `@Tag` and used `@RestClientTest` to mock the server. I also used Instancio to create test data easily. For book data, I used a "Merge or Persist" strategy: update if the book exists, or create if it doesn't. For security, I bought a domain, used Nginx as a reverse proxy, and applied HTTPS with Let's Encrypt.

### Auth and Relationship Design : 2025-11-15

I built a custom auth system with Spring Security to keep users anonymous but allow features. If an anonymous user does certain actions, they are promoted to `Guest`. I used session-based auth to match Thymeleaf. For JPA, I used ID references instead of object references between Aggregates to lower coupling. This made the domain boundaries clear and simplified tests.

### Concurrency and Performance : 2025-11-18

To handle many "Like" requests, I used a "Delayed Write" strategy. The server responds with `202 Accepted` immediately, puts the task in a queue, and a background thread processes them in batches. I used JDBC Template Batch Update to avoid the overhead of JPA's persistence context. I also filtered out repeated On/Off requests in memory to reduce DB load.

### Architecture Refactoring : 2025-11-23

I added Rate Limiting at the Nginx level to prevent attacks. I reorganized packages around the domain to make it easier to find classes. I used `package-private` access modifiers to hide implementation details. For random "Talk" recommendations, I used a "Stale-While-Revalidate" cache. To pick random items quickly, I sampled IDs instead of sorting the whole table. I updated the cache asynchronously using `ThreadPoolTaskExecutor`.

---

## AI Agent Collaboration (2026-01)

After a break, I resumed development with an AI agent to clean up legacy code and add new features.

### Separation of Front/Back and Adding React : 2026-01-20

Thymeleaf was good for prototypes, but it was hard to build complex UI or mobile versions. I split the project into a backend RESTful API and a frontend React project (quiet-chatter-front-end) using TypeScript and Vite. This improved the user experience and made maintenance easier.

### Auth Architecture Update : 2026-01-21

I changed from session-based auth to JWT (JSON Web Token) for mobile compatibility and a stateless server. I used Redis for caching to reduce DB lookups. I also redesigned the "Guest auto-promotion" in a filter so that users without tokens are transparently given temp accounts and tokens.

### Simplifying the Talk Domain : 2026-01-25

I removed complex Value Objects (VO) and embedded types that made JDBC batch processing difficult. I changed time info to a simple `LocalDate` field and changed reaction counts to primitive types. I also added a `boolean` field for the hidden status.

### Auto-Hidden Feature : 2026-01-25

I used Spring Scheduling and JPA Bulk Update (`@Modifying`) instead of a heavy Spring Batch. I added a composite index on the hidden status and date columns to keep searches fast.

### Entity and Time Type Changes : 2026-01-25

I changed time types from `Instant` to `LocalDateTime` (KST) to make development easier. I also added a "last modified" field to all entities to track changes.

### Edit and Delete Features : 2026-01-26

When a "Talk" is edited, its hidden date is automatically extended. For deletion, I used "Soft Delete" (just hiding it) instead of deleting the data. I check if a post was edited by looking for the "last modified" time instead of using a separate column.

### Logging and Monitoring : 2026-01-29

I added an SLF4J MDC filter to track requests. Every HTTP request gets a unique Request ID. The logs now include the method and URI, making it easier to debug issues in a multi-threaded environment.

### Infinite Scroll for Book Search : 2026-01-29

I added infinite scroll to the book search results. The backend was optimized to handle paging efficiently so users can browse without pauses.

### API Spec Automation : 2026-01-30

I built a pipeline using Spring Rest Docs and OpenAPI (Swagger). Only verified API behaviors are documented, so the docs always match the code. The JSON spec is served via an endpoint for tools like Swagger UI. I also standardized the response format.

### Subdomain Auth and Environments : 2026-01-31

I updated the cookie policy so auth works across different subdomains. The cookie domain is set to the parent domain. I also separated configurations for local and production environments (Secure, SameSite, etc.) using type-safe config objects.
