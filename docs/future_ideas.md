# Future Ideas & Improvements

This document tracks ideas for the future of the project, technical improvements, and better collaboration.

## 1. Automation for Frontend Collaboration

The backend provides a reliable `OpenAPI 3.0 (JSON)` spec via the `/v1/spec` endpoint using `Spring Rest Docs` and `restdocs-api-spec`.
We can connect this with the frontend process to improve productivity.

### 1.1 Type and Code Generation

Instead of manually defining types on the frontend, we can use tools to auto-generate them. This creates a cycle: "Backend changes -> Frontend compile error -> Immediate fix."

* **Recommended Tools:**
    * **openapi-typescript:** Reads JSON specs and auto-generates TypeScript `interfaces`. It is light and easy to add.
    * **Orval / TanStack Query (React Query):** Generates both types and `Hooks` (`useQuery`, `useMutation`) for fetching data.

* **Example Workflow:**
    1. A frontend developer runs `npm run api-sync` locally.
    2. The script downloads the latest JSON from `http://api-server/v1/spec`.
    3. `openapi-typescript` updates `src/types/api.d.ts`.
    4. If a type changed (e.g., `Boolean` to `boolean`), a compile error occurs.
    5. The developer notices and fixes it immediately.

### 1.2 Visualization Tools (Documentation UI)

JSON files are hard for humans to read. We can use tools to visualize them.

* **Swagger UI / Redoc:**
    * Run Swagger UI locally or on a separate server, and use the backend's `/v1/spec` URL.
    * This keeps the backend server "light" by not embedding `springdoc-openapi-ui`.

### 1.3 Change Detection and Alerts (Breaking Change Detection)

When the backend is deployed, we can automatically check if any changes break compatibility.

* **OpenAPI Diff:**
    * In the CI pipeline, compare the "previous version spec" with the "current build spec".
    * **If a Breaking Change is found:**
        * Automatically add a warning comment to the PR.
        * Send an alert to Slack or Discord (e.g., "🚨 API Changed: `didLike` field type changed").
    * Tools: `oasdiff`, `openapi-diff`
