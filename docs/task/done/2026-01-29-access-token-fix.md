# Agent Working Procedure
1. Review the code base and prepare for work based on the "## Developer Requirements" section below.
2. If the last section is a developer section like "## Developer Requirements Update" or "## Developer Review Response", start from that part.
3. If you need a decision from the developer, add a section called "## Agent Review Request" at the end of this file and stop working.
4. When work is finished, add a section called "## Agent Work Execution" and describe what you did.

## Developer Requirements
- According to `docs/guide/api_design_guide.md`, JWT should be sent in the header.
- However, the current code checks cookies.
- I want to improve this: check the cookie first, and if it's missing, check the header.
- This will make API testing easier.
- Header-based tokens should not include the Refresh Token.
- First, update the API guide document. Then, update the implementation.
- Update/add related tests. Check `maskun.quietchatter.security`.

## Agent Work Execution: 2026-01-29 10:40
- **Updated API Design Guide (`docs/guide/api_design_guide.md`)**:
    - Specified that authentication prioritizes Cookie, then Header.
    - Specified that Refresh Tokens are only for Cookies.
- **Code Update (`AuthTokenService.java`)**:
    - Updated `extractAccessToken` to check the `access_token` cookie first. If missing, it extracts the Bearer token from the `Authorization` header.
    - Added `extractBearerToken` helper method.
- **Added Tests (`AuthTokenServiceTest.java`)**:
    - Added a test to check token extraction from the header when the cookie is missing.
    - Added a test to check if the cookie is prioritized over the header.
    - All tests passed.
