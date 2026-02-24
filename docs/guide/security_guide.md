# Application Security Guide

This document defines the policies and rules for how the application handles authentication, tokens, and cross-origin resource sharing (CORS).

## 1. Authentication (JWT)

Our application relies on JSON Web Tokens (JWT) for user authentication.

- **Primary Method**: Use HttpOnly Cookies for both the `access_token` and the `refresh_token`. This protects against XSS attacks.
- **Secondary Method**: Use the `Authorization: Bearer <token>` header only for automated testing or specific clients that cannot support cookies.

### 1.1 Implementation Rules:
- The backend must use `OncePerRequestFilter` to extract and validate tokens on every secure request.
- **Security Constraint**: Refresh tokens must **always** be sent using cookies. Do not accept refresh tokens in standard headers or body payloads.

## 2. CORS (Cross-Origin Resource Sharing) Configuration

- CORS policies (like Allowed Origins) are managed dynamically using Spring Profiles (`AppCorsProperties`).
- **Production and Dev Domains**: The list of allowed domains for each environment is defined in the **[infrastructure_policy.md](https://github.com/maskun2/quiet-chatter-docs/blob/main/infrastructure_policy.md)** file inside our shared documentation repository.

### 2.1 Implementation Rules:
- Because we authenticate using HttpOnly cookies, the backend must be configured with `AllowCredentials: true` so that cross-origin web clients can successfully send those cookies.
