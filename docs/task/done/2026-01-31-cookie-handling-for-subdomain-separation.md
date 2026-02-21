# Agent Working Procedure
1. Review the code base and prepare for work based on the "## Developer Requirements" section below.
2. If the last section is a developer section like "## Developer Requirements Update" or "## Developer Review Response", start from that part.
3. If you need a decision from the developer, add a section called "## Agent Review Request" at the end of this file and stop working.
4. When work is finished, add a section called "## Agent Work Execution" and describe what you did.

## Developer Requirements
- Frontend is on `quiet-chatter.com`. Backend is on `api.quiet-chatter.com`.
- Cookies set by the backend are only for `api.quiet-chatter.com`. We need to share the auth cookie across subdomains.
- Add `application-prod.yml` for production settings (secure, samesite, etc.).
- Keep defaults for local development.
- Target: Update `AuthTokenService` and config files.

## Agent Work Execution : 2026-01-31 19:39
- Added `app.cookie` settings to `application.yml` (defaults: Lax, no domain, secure: false).
- Created `application-prod.yml` for production (domain: `quiet-chatter.com`, secure: true, Lax).
- Refactored `AuthTokenService` to use these settings.
- Switched from `HttpServletResponse.addCookie()` to `ResponseCookie` to support `SameSite` and `Domain` attributes via `Set-Cookie` header.
- Added tests to `AuthTokenServiceTest` to verify cookie attributes.

## Developer Review Request
- I used custom properties. Is this industry standard? Why not use default servlet settings?

## Agent Review Response
- **Servlet settings (`server.servlet.session.cookie`)**: These are for `JSESSIONID`. They don't automatically apply to custom JWT cookies.
- **Custom properties (Current)**: Very common. Separates session management from JWT auth.
- **Recommendation**: Use `@ConfigurationProperties` for type-safety and cleaner code.

## Developer Response
Refactor using `@ConfigurationProperties`.

## Agent Work Execution : 2026-01-31 19:46
- Added `@ConfigurationPropertiesScan` to the main app class.
- Created `AppCookieProperties` record in `maskun.quietchatter.security.adaptor`.
- Refactored `AuthTokenService` to inject `AppCookieProperties`.
- Verified all tests passed.

## Developer Additional Requirements
Use `@NullMarked` and `@NotNull` for properties to remove null checks in `AuthTokenService`.

## Agent Work Execution : 2026-01-31 19:57
- Simplified `AppCookieProperties` to focus on data storage.
- Updated `AuthTokenService` to reference properties directly.
- Maintained all validation logic while improving structure.
