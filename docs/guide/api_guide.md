# API Implementation Guide

This document defines the rules for designing and implementing the RESTful API in the `quiet-chatter` backend project.
For detailed API policies (authentication flow, shared specs), refer to the shared documentation repository (*
*[quiet-chatter-docs](https://github.com/maskun2/quiet-chatter-docs)**).

## 1. URI Design

### 1.1 Basic Structure

- Use **kebab-case** (lowercase, hyphens) for URIs.
- Use **plural nouns** for resources (e.g., `/books`, `/talks`).
- No trailing slashes.

### 1.2 Versioning

- Use **URI Path Versioning**: `/v{version_number}/resource` (e.g., `/v1/talks`).
- Ensure backward compatibility when introducing new versions.

## 2. HTTP Methods

- **GET**: Retrieve resources (Idempotent).
- **POST**: Create resources or execute commands (Non-idempotent).
- **PUT**: Replace a resource entirely (Idempotent).
- **DELETE**: Remove a resource (Idempotent).
- **Avoid PATCH**: Prefer explicit POST commands or PUT for updates in this project.

## 3. Request & Response Format

### 3.1 JSON Convention

- **Request/Response Body**: `application/json`
- **Field Naming**: **camelCase** (matches Java fields 1:1).
- **Date/Time**: ISO-8601 string format.

### 3.2 Response Status

- `200 OK`: Success (with body).
- `201 Created`: Resource created successfully (return ID/URI).
- `202 Accepted`: Async processing started.
- `204 No Content`: Success (no body).

### 3.3 Pagination

- Follow Spring Data's `Pageable` convention.
- **Request**: `?page=0&size=10&sort=createdAt,desc`
- **Response**: Include `content` list and `page` metadata.

## 4. Error Handling (RFC 7807)

Use Spring MVC's `ProblemDetail` for standardized error responses.

```json
{
  "type": "about:blank",
  "title": "Bad Request",
  "status": 400,
  "detail": "Invalid ISBN format.",
  "instance": "/v1/books"
}
```

- **400 Bad Request**: Validation failure.
- **401 Unauthorized**: Missing/Invalid token.
- **403 Forbidden**: Valid token but insufficient permissions.
- **404 Not Found**: Resource does not exist.
- **500 Internal Server Error**: Unexpected server error.

## 5. Security Implementation

### 5.1 Authentication (JWT)

- **Primary Method**: HttpOnly Cookies (`access_token`, `refresh_token`).
- **Secondary Method**: `Authorization: Bearer <token>` header (for testing/clients without cookie support).
- **Implementation**:
    - Use `OncePerRequestFilter` to extract and validate tokens.
    - Refresh tokens are strictly cookie-only for security.

### 5.2 CORS Configuration

- CORS policies (Allowed Origins) are managed via Spring Profiles (`AppCorsProperties`).
- **Production/Dev Domains**: Defined in *
  *[infrastructure_policy.md](https://github.com/maskun2/quiet-chatter-docs/blob/main/infrastructure_policy.md)** in the
  shared docs.
- **Implementation**: Ensure `AllowCredentials: true` is set for cookie support.

## 6. API Documentation Strategy

We use **Test-Driven Documentation** to guarantee accuracy.

### 6.1 Tools

- **Spring Rest Docs**: Generates snippets from tests.
- **restdocs-api-spec**: Converts snippets to **OpenAPI 3.0 (JSON)**.

### 6.2 Workflow

1. **Write Test**: Use `@RestClientTest` or `@WebMvcTest` with `RestDocs` support.
2. **Build**: Running `./gradlew bootJar` automatically executes tests and generates `openapi3.json`.
3. **Deploy**: The JSON spec is bundled into the JAR and served at `/v1/spec`.
4. **No Manual Edits**: Never modify the JSON file manually. Fix the code or test instead.
