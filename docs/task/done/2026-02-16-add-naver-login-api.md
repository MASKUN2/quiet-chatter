# Agent Working Procedure
1. Review the code base and prepare for work based on the "## Developer Requirements" section below.
2. If the last section is a developer section like "## Developer Requirements Update" or "## Developer Review Response", start from that part.
3. If you need a decision from the developer, add a section called "## Agent Review Request" at the end of this file and stop working.
4. When work is finished, add a section called "## Agent Work Execution" and describe what you did.

## Developer Requirements
- I want to add OAUTH2 Naver Login.
- API spec: https://developers.naver.com/docs/login/api/api.md
- Endpoint: `/v1/auth/login/naver`
- Design the interface between Client and Server first.

## Agent Review Request: Interface Design
- **Flow**: Client redirects to Naver -> Naver redirects to Client with `code` -> Client calls Server `POST /v1/auth/login/naver` -> Server gets token from Naver -> Server gets profile -> Server registers/logs in user -> Server returns JWT in cookie.
- **API**: `POST /v1/auth/login/naver` with `{code, state}`. Returns `{isNewMember, nickname}`.
- **Model**: Add `provider`, `providerId` to Member entity.

## Developer Response
- Don't merge guest accounts.
- Callback path: `https://quiet-chatter.com/auth/login/naver/callback`.
- Naver config is already set in `application.yml`.

## Agent Work Execution : 2026-02-16 13:20
### Backend
- Added `OauthProvider` and `providerId` to `Member`.
- Created Flyway migration `V3__member_oauth_fields.sql`.
- Implemented `NaverClient` for token and profile APIs.
- Implemented `AuthMemberService.loginWithNaver`.
- Added `AuthLoginApi` for `/v1/auth/login/naver`.
### Frontend
- Added `loginWithNaver` to `api.ts`.
- Implemented `NaverCallback.tsx` and updated routing.
- Updated `Header.tsx` for login status.

## Developer Additional Requirements
- Add index for OAuth columns in Member entity.
- Success response should be `204 No Content` with no body.

## Agent Work Execution : 2026-02-16 13:40
- Added composite index to `Member`.
- Updated `AuthLoginApi` to return `204`.
- Updated frontend to handle empty response.

## Developer Additional Requirements
- Merge SQL scripts into `V3`.
- Squash commits.
- Create logout API (204 response). Remove refresh token from cache.
- Separate refresh token cache logic into a component.

## Agent Work Execution : 2026-02-16 14:00
- Created `RefreshTokenCache` and `RefreshTokenRedisCache`.
- Refactored `AuthTokenService` to use the cache.
- Implemented `AuthLogoutApi` (`POST /v1/auth/logout`).
- Updated tests.
