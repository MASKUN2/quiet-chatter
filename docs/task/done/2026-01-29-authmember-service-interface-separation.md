# Agent Working Procedure
1. Review the code base and prepare for work based on the "## Developer Requirements" section below.
2. If the last section is a developer section like "## Developer Requirements Update" or "## Developer Review Response", start from that part.
3. If you need a decision from the developer, add a section called "## Agent Review Request" at the end of this file and stop working.
4. When work is finished, add a section called "## Agent Work Execution" and describe what you did.

## Developer Requirements
- `AuthMemberService` uses `RedisTemplate` directly.
- This is hard to test and depends too much on one implementation.
- Separate this using an interface.
- Define an outbound port interface in `maskun.quietchatter.security` and put the implementation in `internal`.
- Update test codes.

## Agent Work Execution : 2026-01-29 11:15
- **Interface Separation**: Defined the `AuthMemberCache` interface in `maskun.quietchatter.security`.
- **Implementation Separation**: Created `AuthMemberRedisCache` in `maskun.quietchatter.security.internal` and moved Redis logic there.
- **Service Refactoring**: Updated `AuthMemberService` to depend on `AuthMemberCache` instead of `RedisTemplate`.
- **Test Refactoring**: 
    - Changed `AuthMemberServiceTest` from a `@SpringBootTest` to a unit test using Mockito for speed.
    - Added an integration test for the Redis logic in `AuthMemberRedisCacheTest`.
- **Verification**: Confirmed everything works correctly.
