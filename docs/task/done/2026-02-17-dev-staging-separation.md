# Agent Working Procedure
1. Review the code base and prepare for work based on the "## Developer Requirements" section below.
2. If the last section is a developer section like "## Developer Requirements Update" or "## Developer Review Response", start from that part.
3. If you need a decision from the developer, add a section called "## Agent Review Request" at the end of this file and stop working.
4. When work is finished, add a section called "## Agent Work Execution" and describe what you did.

## Developer Requirements
- Currently, when a `dev` branch is pushed, we want to push a Docker image tagged `:latest` to Docker Hub as `quiet-chatter-dev`.
- Investigate the server (`3.37.208.126`) and Nginx settings.
- Create a new database in the existing PostgreSQL container for `dev`.
- Update Docker Compose and Nginx to support the `dev` environment.

## Agent Work Execution : 2026-02-17 22:30
1. **CI/CD Workflow**: Created `.github/workflows/dev-deploy.yml` for automatic `dev` branch deployment.
2. **Database Separation**: Created `quiet_chatter_dev` database in the PostgreSQL container.
3. **Redis Separation**: Configured `dev` to use Redis DB `1` while `prod` uses `0`.
4. **Docker & Nginx**:
    - Added `app-dev` service (port 8081) to `docker-compose.yml`.
    - Added `dev-api.quiet-chatter.com` routing to Nginx.
5. **App Profiling**: Created `application-dev.yml` and `application-prod.yml` for environment-specific settings.
6. **Cloudflare Integration**: Configured Nginx to trust Cloudflare IPs and added a cron job to auto-update the IP list.
