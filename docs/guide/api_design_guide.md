# API Design Guide

This document defines the rules for designing the RESTful API in this project.
The goal is to provide a consistent interface that is easy to use and maintain.

## 1. URI Design

### 1.1 Basic Structure

- Use only **lowercase** for all APIs.
- Use **hyphens (-, kebab-case)** to separate words.
- Use **plural nouns** for resources.
- Do not include a trailing slash (/) at the end of the URL.

### 1.2 Versioning

- Use **URI Path Versioning** to ensure backward compatibility.
- Include the version in the path: `/v{version_number}/`.

## 2. HTTP Methods

Use the correct HTTP method to describe the action on a resource.

- **GET** (Read): Get the status of a resource. (Idempotent)
- **POST** (Create): Create a new resource or run a controller-style command. (Not Idempotent)
- **PUT** (Update): Update the entire resource. (Idempotent). We avoid using PUT for creating resources.
- **DELETE** (Delete): Delete a resource. (Idempotent)
- ~~PATCH~~ : Avoid using PATCH in this project.

## 3. Request & Response

### 3.1 Format

- Use **JSON** for the body of requests and responses.
- `Content-Type: application/json`

### 3.2 Naming Convention (JSON)

- Use **CamelCase (lowerCamelCase)** for JSON field names.
    - *Note: This maps 1:1 with Java object field names.*
- Use ISO-8601 standard strings for date/time fields.

### 3.3 Success Response

- **Returning a Resource**: Return the resource object with status `200 OK`.
- **Created Successfully**: Return the identifier of the created resource with status `201 Created`.
- **Accepted (Async)**: Use `202 Accepted` if the task is not finished immediately or there is nothing to return. (No Body)
- **No Content**: Use `204 No Content` if there is clearly nothing to return after a delete or update. (No Body)

### 3.4 Pagination

- Use Spring Data's `Pageable` interface rules.
- Query Parameters:
    - `page`: Page number (starts from 0).
    - `size`: Items per page.
    - `sort`: Sorting rule (e.g., `createdAt,desc`).
- Response Structure: Includes a `content` array and `page` metadata.

## 4. Error Handling

### 4.1 Format (RFC 7807)

- Use Spring MVC's `ProblemDetail` object (RFC 7807) for error responses.
- This helps the client clearly understand the cause of the error.

### 4.2 Main Fields

- **type**: A URI that identifies the error type (Default: "about:blank").
- **title**: A short description of the error type.
- **status**: HTTP status code.
- **detail**: A specific explanation of the error (Must be in simple English).
- **instance**: The URI where the error happened.

### 4.4 Common Error Status Codes

- **400 Bad Request**: The request is wrong (e.g., validation failed).
- **401 Unauthorized**: Authentication is required.
- **403 Forbidden**: You don't have permission.
- **404 Not Found**: The resource could not be found.
- **500 Internal Server Error**: Internal server error.

## 5. Authentication

- Use **JWT (JSON Web Token)** based authentication.
- **Token Check Order**:
  1. **Cookie**: `access_token` cookie (Priority)
  2. **Header**: `Authorization: Bearer <token>` header (Secondary, for tests etc.)
- **Refresh Token**: Managed only via `refresh_token` cookie (HttpOnly, Secure) for security.
- **OAuth (Naver)**: 
  - Login via `/v1/auth/login/naver`. If not a member, returns `200 OK` with a `Register Token`.
  - Sign up via `/v1/auth/signup/naver`. Validates `Register Token` and issues full tokens.

```text
# Header example (if cookies cannot be used)
Authorization: Bearer <access_token>
```

## 6. CORS Policy

Control allowed origins strictly for each environment.

- **Basic Policy**: `AllowCredentials: true` to support cookie-based authentication.
- **Allowed Origins by Environment**:
    - **Production**: `https://quiet-chatter.com`
    - **Development**: `https://dev.quiet-chatter.com`, `http://localhost:5173`
    - **Local**: `http://localhost:5173`, `http://127.0.0.1:5173`

## 7. API Documentation

### 7.1 Documentation Strategy

- Use **Spring Rest Docs** and **restdocs-api-spec** to auto-generate docs based on tests.
- This ensures the docs are always correct because they only generate if tests pass.
- The final result is provided in **OpenAPI 3.0 (Swagger) JSON** format.

### 7.2 Automation Flow

1. **Write Tests**: Write test code using `RestDocs` (`@Tag("restdocs")`) for each API endpoint.
2. **Auto-Generate & Package**: These happen automatically during build (`bootJar` task):
    - Run `testDocs` task (runs only documentation tests).
    - Run `openapi3` task (creates `build/api-spec/openapi3.json`).
    - Include the JSON file in `static/docs/` inside the JAR.
3. **CI/CD & Deployment**: GitHub Actions builds the JAR with the spec. AWS Watchtower updates the server.
4. **No Manual Commits**: Do not commit the `openapi3.json` file to Git. It is created dynamically during build.

### 7.3 Accessing the Spec

- **Production**: Visit `GET /v1/spec` to see the latest JSON spec on the server.
- **Local**: Run `./gradlew openapi3` and check `localhost:8080/v1/spec`.

### 7.4 Required Information

You must include these in your tests:

- Request/Response Fields (Field Descriptor)
- Path Variables
- Query Parameters
- Required status and descriptions