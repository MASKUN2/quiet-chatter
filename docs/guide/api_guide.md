# API Implementation Guide

This document defines the rules for designing and implementing the RESTful API in the `quiet-chatter` backend project.
For detailed API policies (like authentication flows and shared specifications), please refer to the shared documentation repository: **[quiet-chatter-docs](https://github.com/maskun2/quiet-chatter-docs)**.

## 1. URI Design

### 1.1 Basic Structure

- Use **kebab-case** (lowercase letters separated by hyphens) for URIs.
- Use **plural nouns** for resource names (e.g., `/books`, `/talks`).
- Do not add a trailing slash (`/`) at the end of URIs.

### 1.2 Versioning

- Use **URI Path Versioning**: Include the version number in the path as `/v{version_number}/resource` (e.g., `/v1/talks`).
- Always maintain backward compatibility when creating new versions.

## 2. HTTP Methods

- **GET**: Retrieve a resource (Idempotent).
- **POST**: Create a new resource or execute a command (Non-idempotent).
- **PUT**: Fully replace an existing resource (Idempotent).
- **DELETE**: Delete a resource (Idempotent).
- **Avoid PATCH**: Prefer to use explicit POST commands or PUT requests for updates.

## 3. Request and Response Formats

### 3.1 JSON Convention

- **Content Type**: Use `application/json` for both request and response bodies.
- **Field Naming**: Use **camelCase** for JSON fields. This should match the Java field names exactly.
- **Date and Time**: Use the ISO-8601 string format for dates and times.

### 3.2 Response Status Codes

- `200 OK`: Request was successful (includes a response body).
- `201 Created`: Resource was created successfully. Return the ID or URI of the new resource.
- `202 Accepted`: Asynchronous processing has started.
- `204 No Content`: Request was successful, but there is no response body to return.

### 3.3 Pagination

- Follow the `Pageable` convention provided by Spring Data.
- **Request Example**: `?page=0&size=10&sort=createdAt,desc`
- **Response**: The response must include a `content` list containing the items and a `page` object containing pagination metadata.

## 4. Error Handling (RFC 7807)

Use Spring MVC's `ProblemDetail` class to generate standardized error responses.

```json
{
  "type": "about:blank",
  "title": "Bad Request",
  "status": 400,
  "detail": "Invalid ISBN format.",
  "instance": "/v1/books"
}
```

- **400 Bad Request**: Validation failed (e.g., missing fields or invalid format).
- **401 Unauthorized**: Missing or invalid authentication token.
- **403 Forbidden**: Valid token, but the user does not have permission.
- **404 Not Found**: The requested resource does not exist.
- **500 Internal Server Error**: An unexpected server error occurred.

## 5. Security Implementation

### 5.1 Authentication (JWT)

- **Primary Method**: Use HttpOnly Cookies for both `access_token` and `refresh_token`.
- **Secondary Method**: Use the `Authorization: Bearer <token>` header (only for testing or clients that do not support cookies).
- **Implementation Rules**:
    - Use `OncePerRequestFilter` to extract and validate tokens.
    - Refresh tokens must be sent using cookies only, for security reasons.

### 5.2 CORS Configuration

- CORS policies (Allowed Origins) are managed using Spring Profiles (`AppCorsProperties`).
- **Production and Dev Domains**: These are defined in the **[infrastructure_policy.md](https://github.com/maskun2/quiet-chatter-docs/blob/main/infrastructure_policy.md)** file in the shared documentation.
- **Implementation Rules**: Ensure `AllowCredentials: true` is configured so that cross-origin requests can include cookies.

## 6. API Documentation Strategy

We use **Test-Driven Documentation** to make sure our API documentation is always accurate.

### 6.1 Tools

- **Spring RestDocs**: Generates documentation snippets from passing tests.
- **restdocs-api-spec**: Converts these snippets into an **OpenAPI 3.0 (JSON)** specification file.

### 6.2 Workflow

1. **Write the Test**: Write your tests using `@RestClientTest` or `@WebMvcTest` with support for `RestDocs`.
2. **Build**: Run the command `./gradlew bootJar`. This automatically runs the tests and creates the `openapi3.json` file.
3. **Deploy**: The generated JSON file is packed into the JAR file and served automatically at `/v1/spec`.
4. **No Manual Edits**: **Never** edit the generated `openapi3.json` file manually. If the documentation is wrong, update your code or test instead.
