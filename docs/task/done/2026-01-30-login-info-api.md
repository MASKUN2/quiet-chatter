# Agent Working Procedure
1. Review the code base and prepare for work based on the "## Developer Requirements" section below.
2. If the last section is a developer section like "## Developer Requirements Update" or "## Developer Review Response", start from that part.
3. If you need a decision from the developer, add a section called "## Agent Review Request" at the end of this file and stop working.
4. When work is finished, add a section called "## Agent Work Execution" and describe what you did.

## Developer Requirements
Create an API to retrieve user login information. `GET /api/v1/auth/me` should return:
```json
{
  "isLoggedIn": "Boolean",
  "id": "nullable String (authMemberId)",
  "role": "String (role or 'anonymous')"
}
```
Put this in the `security` package.

## Agent Work Execution : 2025-01-30 16:40
Improved the login information API to use `@AuthenticationPrincipal`.
1. **Updated AuthMeApi**: Changed from direct `SecurityContextHolder` reference to `@AuthenticationPrincipal AuthMember` injection.
2. **Cleanup**: Removed unused imports and simplified logic.
3. **Location**: Updated `AuthMeApi` in `maskun.quietchatter.security.adaptor`.
