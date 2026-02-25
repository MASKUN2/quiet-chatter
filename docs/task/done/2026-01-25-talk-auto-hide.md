# Agent Working Procedure
1. Review the code base and prepare for work based on the "## Developer Requirements" section below.
2. If the last section is a developer section like "## Developer Requirements Update" or "## Developer Review Response", start from that part.
3. If you need a decision from the developer, add a section called "## Agent Review Request" at the end of this file and stop working.
4. When work is finished, add a section called "## Agent Work Execution" and describe what you did.

## Developer Requirements
1. Do not modify the code until the review of this plan is finished.
2. Target entity: [Talk.java](../../../src/main/java/maskun/quietchatter/talk/domain/Talk.java)
3. Every day at midnight (00:00), change the `isHidden` flag to `true` for "Talk" entities where `dateToHidden` has passed.
4. Use Spring Scheduling to run this at the set time. Use JDBC Template for bulk updates instead of JPA.
5. Review this design. If there are better ways, add an "## Agent Review Execution" section. Mention trade-offs and industry standards.

## Agent Review Execution
Using **Spring Scheduling + JDBC Template** for bulk updates is a **very good and reasonable choice** for the current project size.

### 1. Review of Proposed Method
* **Pros**:
    * **Performance**: JPA's Dirty Checking is heavy for thousands of records. JDBC Template is much faster and uses fewer resources.
    * **Simplicity**: Easy to maintain without complex batch frameworks.
* **Cons**:
    * **Distributed Environments**: If you have multiple servers, the task might run multiple times. You might need `ShedLock` or DB-level locks.
    * **Transaction Size**: If there are millions of records, a single transaction might cause DB lock issues (not a problem for now).

### 2. Alternatives
#### Option A: Spring Batch (Not recommended)
* Too much overhead for a simple update task.
#### Option B: JPA @Modifying Query (Recommended)
* Use `@Modifying` and `@Query` in `TalkRepository`.
* **Pros**: Easier to keep the persistence layer consistent. It is more common in Spring Data JPA environments.

### 3. Industry Standards & Conclusion
Industry standard for simple status changes is **Spring Scheduling + Bulk Update (JPA @Modifying or JDBC Template)**.

**Final Recommendation**:
1. Use `TalkRepository` with a `@Modifying` query for better cohesion.
2. Consider `ShedLock` for future server scaling.
3. Add a composite index on `date_to_hidden` and `is_hidden`.

## Developer Response
1. Proceed with the code.
2. Use **Spring Scheduling + JPA @Modifying Query**.
3. Add an index for the columns used in search.
4. Separate the repository for bulk operations to keep concerns separate.
5. Write tests.

## Agent Work Execution
1. **Entity Update**: Added composite index `idx_talk_date_to_hidden_is_hidden` to `Talk.java`.
2. **Repository Creation**: Created `TalkBulkRepository` with a `@Modifying` query to hide expired talks.
3. **Scheduler Implementation**: Created `TalkAutoHiddenProcessor` to run the task every day at midnight.
4. **Configuration**: Added `@EnableScheduling` to the main application class.
5. **Testing**: Wrote `TalkBulkRepositoryTest` to verify the logic.
