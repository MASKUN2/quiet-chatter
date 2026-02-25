# Agent Working Procedure
1. Review the code base and prepare for work based on the "## Developer Requirements" section below.
2. If the last section is a developer section like "## Developer Requirements Update" or "## Developer Review Response", start from that part.
3. If you need a decision from the developer, add a section called "## Agent Review Request" at the end of this file and stop working.
4. When work is finished, add a section called "## Agent Work Execution" and describe what you did.

## Developer Requirements
1. Target entity: [Talk.java](../../../src/main/java/maskun/quietchatter/talk/domain/Talk.java)
2. The `time` in this entity means the auto-hide time. It currently uses `@Embedded`, but I want to change it to a simpler `LocalDate` field.
3. Add a `boolean` flag `isHidden` to the entity to show if the post is hidden.
4. Create two constructors: one that receives the hide date and one that doesn't.
5. If no hide date is provided, the default should be 12 months after the current time.

## Agent Work Execution
### Talk Domain Simplification
- Changed the `Time` embedded type in the `Talk` entity to a `LocalDate dateToHidden` field.
- Added a `boolean isHidden` field.
- Created two constructors to handle cases with and without a defined `dateToHidden` (defaulting to 12 months).
- Updated `TalkCreateRequest`, services, and API layers to use `LocalDate` instead of the `Time` object.
- Added a temporary `getHiddenTimeAsInstant()` method to the `Talk` entity for compatibility with `TalkResponse`.

## Developer Requirements Update
- Change the field name `timeToHidden` in the `Talk` entity to `dateToHidden`.

## Agent Work Execution
### Field Name Change
- Changed the field name from `timeToHidden` to `dateToHidden` in the `Talk` entity.
- Updated the database column name from `time_to_hidden` to `date_to_hidden`.
- Updated constructors and the `toString()` method.
- Updated `TalkCreateRequest`, `TalkCreateService`, `TalkCommandApi`, `TalkResponse`, and `TalkResponseMapper` to use `dateToHidden`.

## Developer Requirements Update
Add tests to verify the changes.

## Agent Work Execution
### Added Test Code
- Created the `TalkTest` class to verify constructor logic:
    - Checked if hide date defaults to 12 months if not specified.
    - Checked if specified hide dates are set correctly.
    - Verified the initial value of `isHidden` (false).
- Updated `TalkCreateServiceTest` to use `LocalDate`.

## Developer Requirements Update
- Simplify the `ReactionCount reactionCount` field. Change it from an embedded type to two separate fields. Use column names from the `AttributeOverrides` annotation.
- This field is used for searches and async updates, so review the impact carefully.

## Agent Work Execution
### Reaction Count Simplification
- Removed the `ReactionCount` embedded type from the `Talk` entity and added `long likeCount` and `long supportCount` fields.
- Updated constructors and `toString()` in the `Talk` entity.
- Updated `RecommendTalkQueryApi` and `TalkResponseMapper` to use the new count fields directly.
- Verified that `ReactionBatchWorker` logic is compatible with the new column names.
- (Manual action recommended: Delete the unused `ReactionCount.java` file.)
