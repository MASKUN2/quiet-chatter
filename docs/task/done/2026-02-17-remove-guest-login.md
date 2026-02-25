# Agent Working Procedure
1. Review the code base and prepare for work based on the "## Developer Requirements" section below.
2. If the last section is a developer section like "## Developer Requirements Update" or "## Developer Review Response", start from that part.
3. If you need a decision from the developer, add a section called "## Agent Review Request" at the end of this file and stop working.
4. When work is finished, add a section called "## Agent Work Execution" and describe what you did.

## Developer Requirements
- Remove `AnonymousToGuestPromotionFilter`. We no longer want guest accounts now that Naver login is here.
- Unauthenticated users should stay as Spring Security anonymous users.
- Delete the `GUEST` role.

## Agent Review Request
- **Plan**: Delete filter, update `SecurityConfig`, remove `Role.GUEST`, remove guest methods from `Member` and services.
- **Question**: Should anonymous users still be allowed to write/react? Recommendation: Force login for these actions.
- **Question**: What to do with existing guest data?

## Developer Response
- Force login for POST/PUT/DELETE actions.
- Promote existing `GUEST` users to `REGULAR` status.
- Skip frontend changes for now.

## Agent Work Execution : 2026-02-17 17:10
1. **Security Settings**: Deleted the filter and updated `SecurityConfig` to require authentication for all state-changing requests.
2. **Cleanup**: Removed `GUEST` from `Role.java` and deleted all guest-related methods in `Member` and services.
3. **Migration**: Created `V5__promote_guest_to_regular.sql` to upgrade all guests to regular members.
4. **Tests**: Updated `JwtAuthenticateTest` and `AuthMeApiTest` for anonymous user behavior. All tests passed.
