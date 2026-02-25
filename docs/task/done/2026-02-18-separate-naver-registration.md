# Agent Working Procedure
1. Review the code base and prepare for work based on the "## Developer Requirements" section below.

## Developer Requirements
- Separate the registration process for Naver users.
- Currently, users are registered during login. Separate this into a proper registration flow.

## Agent Work Execution : 2026-02-18 19:30
1. **Registration Service**: Created `MemberRegistrationService` to handle the actual creation of new members.
2. **Logic Separation**: 
    - Updated `AuthMemberService` to only handle authentication.
    - If user is not found, it returns information for registration instead of creating a member.
3. **API Update**: Updated `AuthLoginApi` to return registration-related data if the user is a new member.
4. **Tests**: Added unit tests for the registration flow and updated existing login tests.
