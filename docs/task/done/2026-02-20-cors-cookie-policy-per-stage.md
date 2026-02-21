# Agent Working Procedure
1. Review the code base and prepare for work based on the "## Developer Requirements" section below.
2. If the last section is a developer section like "## Developer Requirements Update" or "## Developer Review Response", start from that part.
3. If you need a decision from the developer, add a section called "## Agent Review Request" at the end of this file and stop working.
4. When work is finished, add a section called "## Agent Work Execution" and describe what you did.

## Developer Requirements
- Separate CORS and Cookie settings for Production and Development.
- **Production**: domain `quiet-chatter.com`.
- **Development**: domain `dev.quiet-chatter.com` and `localhost:5173`.
- Use Spring Profiles instead of hardcoding.

## Agent Work Execution : 2026-02-20 18:30
1. **Properties**: Created `AppCorsProperties.java` for type-safe CORS lists.
2. **Environment config**:
    - `application-dev.yml`: Added `dev.quiet-chatter.com` and `localhost`.
    - `application-prod.yml`: Added `quiet-chatter.com`.
3. **Security**: Refactored `SecurityConfig.java` to use the injected properties list for CORS.
4. **Docs**: Updated `api_design_guide.md` with the new policies.
5. **Tests**: Added `CorsConfigTest.java` to verify origin filtering.
