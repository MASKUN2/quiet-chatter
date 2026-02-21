# Agent Working Procedure

1. Review the code base and prepare for work based on the `## Developer Requirements` section below.
2. If the last section is a developer section like `## Developer Requirements Update` or `## Developer Review Response`, skip the middle and start from there.
3. If you need a decision from the developer, add a section called `## Agent Review Request` at the end of this file and stop working.
4. When work is finished, add a section called `## Agent Work Execution : {yyyy-MM-dd HH:mm}` at the end of this file and describe what you did.
5. When updating documents, **do not delete or overwrite** existing requirements, plans, or reviews. Always add new information to the **end** of the file.
6. When making a plan, do not give multiple choices like "A or B". Specify **one clear implementation path**.
7. For other general rules, check [ai_agent_guide.md](/docs/guide/ai_agent_guide.md).

## Developer Requirements
- In `AuthLoginApiTest`, the Naver profile nickname is used if the member is not registered. However, we do not actually collect Naver nicknames.
- Change it to use `RandomNickNameSupplierImpl` to create a nickname instead.
- Remove unnecessary null checks in `MemberService.createNewNaverMember` (always expect a non-null value).
- Refactor `RandomNickNameSupplier` so it can be called from adapters (like `AuthLoginApi`).
- Apply JSpecify's `@NullMarked` for explicit null safety.

## Agent Work Plan
1. **Move `RandomNickNameSupplier`**:
    - Move from `member.application.out` to `member.application.in` to allow external access.
2. **Modify `MemberService` and `MemberRegistrable`**:
    - Remove nickname null check logic in `createNewNaverMember`.
    - Apply `@NullMarked` and ensure parameters are non-null.
3. **Modify `AuthMemberService` and `NaverProfile`**:
    - Remove `nickname` field from `NaverProfile` record.
    - Stop collecting nickname when fetching Naver profile in `AuthMemberServiceImpl`.
4. **Modify `AuthLoginApi`**:
    - Inject `RandomNickNameSupplier` to create and return a temp nickname for non-members.
5. **Update Test Code**:
    - Remove nickname from `NaverProfile` creation in `AuthLoginApiTest` and verify temp nickname logic.
    - Remove null input test cases in `MemberServiceTest`.

## Agent Work Execution : 2026-02-20 19:30

### 1. Nickname Generation Refactoring and Relocation
- **Move `RandomNickNameSupplier`**: Moved from `member.application.out` to `member.application.in` so the external adapter (`AuthLoginApi`) can use it directly.
- **Update `RandomNickNameSupplierImpl`**: Updated imports after moving the interface.

### 2. Simplifying Member Service
- **`MemberService.java`**: 
    - Removed unnecessary `nickname` null checks and the `RandomNickNameSupplier` dependency from `createNewNaverMember`.
    - `MemberService` now trusts and uses the provided nickname.
- **`MemberRegistrable.java` & `MemberQueryable.java`**: Applied `@NullMarked` to declare null safety.

### 3. Improving Naver Login Process
- **`AuthMemberService.java` & `AuthMemberServiceImpl.java`**: 
    - Removed the `nickname` field from the `NaverProfile` record.
    - Stopped collecting nicknames during Naver profile lookup.
- **`AuthLoginApi.java`**:
    - Injected `RandomNickNameSupplier`. For non-members, it now creates a temp nickname and returns it in `NaverLoginResponse`.

### 4. Null Safety and Style Guide Update
- **`docs/guide/code_style_guide.md`**: Added rules for using JSpecify's `@NullMarked`.
- **Global `@NullMarked` Application**: Applied `@NullMarked` to main interfaces like `MemberRepository`, `AuthMemberCache`, and `AuthMemberService`.

### 5. Test Code Updates and Verification
- **`AuthLoginApiTest.java`**: Updated for the new `NaverProfile` structure and added Mock logic to check if a random nickname is returned for non-members.
- **`MemberServiceTest.java`**: Removed nickname creation tests since `MemberService` no longer has that responsibility.
- **`AuthMemberServiceTest.java`**: Removed nickname verification from `loginWithNaver` results.
- **Verification**: All related tests passed successfully.
