# Agent Working Procedure
1. Review the code base and prepare for work based on the "## Developer Requirements" section below.
2. If the last section is a developer section like "## Developer Requirements Update" or "## Developer Review Response", start from that part.
3. If you need a decision from the developer, add a section called "## Agent Review Request" at the end of this file and stop working.
4. When work is finished, add a section called "## Agent Work Execution" and describe what you did.

## Developer Requirements
- This task is for analysis only. Do not modify the code until requested.
- We want to collect user events in this project.
- The developer found two ways: using `slf4j + MDC + filter` to log requests/responses, or using `AOP`.
- Analyze which method is better considering the project size and maintenance.

## Agent Work Execution : 2026-01-29 14:30
Based on the **Hexagonal Architecture**, **maintainability**, and **Clean Code principles**, here is the analysis:

### 1. Detailed Analysis

| Item | Filter + MDC (Req/Res Logging) | AOP (Business Event Logging) |
| :--- | :--- | :--- |
| **Goal** | Technical Audit at HTTP level | Collect business actions (User intent) |
| **Pros** | - Logs all requests without missing any <br> - Catches errors before they reach the Controller <br> - Easy to trace the full request flow with MDC | - Selective logging reduces noise <br> - Easy to extract details from method arguments <br> - Can add business meaning (e.g., 'Create Talk') |
| **Cons** | - Overhead from recreating streams for Body logging <br> - Data overload if logging everything (GET, Health Checks) <br> - Needs JSON parsing to understand business context | - Cannot catch errors in the Filter layer <br> - Pointcut management adds a separate maintenance point |
| **Maintenance** | Completely separate from business logic (Adapter layer) | Can be affected by business logic changes (method signature etc.) |

### 2. Consideration for "Quiet Chatter"
- **Hexagonal Architecture**: Filters are in the "Web Adapter" infrastructure, while AOP targets the "Application Layer" or "Web Adapter".
- **Clean Code**: This project has strict rules about method arguments (max 3) and responsibilities. Logging code should be separated as a Cross-cutting Concern.

### 3. Final Recommendation: Hybrid Approach (MDC Filter + AOP)
I recommend using **MDC Filter for basic tracing** and **AOP for selective business events**.

1. **Filter + MDC (TraceID & Basic Info)**:
    - Issue a `traceId` for all requests and store it in MDC.
    - Log a summary `[Method, URI, Status, Time]` at the start/end of a request (no Body).
    - This provides a foundation for monitoring and debugging.

2. **AOP (Detailed Business Events)**:
    - Apply to Application Service or Controller methods that change state (`POST/PUT/DELETE`).
    - Use a custom annotation like `@UserEvent(name="TALK_CREATE")`.
    - This makes it easy to collect specific user actions into an "Event Log" file or DB.

**Conclusion: Using AOP to record "who did what meaningful action" is better for maintenance and data usage as the SNS service grows, rather than just copying every request/response.**

## Developer Additional Follow-up
Can a method like `@UserEvent(name="TALK_CREATE")` carry enough context information?
