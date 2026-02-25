# 2026-02-20 Auto-Deploy and Release Workflow

## Overview
Improved the CI/CD pipeline to automate the release process and separated deployment workflows for development and production.

## Key Changes
### 1. Workflow Separation
- **`release-please`**: Automated versioning and CHANGELOG generation.
- **`deploy-dev.yml`**: Auto-deploy to the dev server on `dev` branch push.
- **`deploy-prod.yml`**: Auto-deploy to the production server on release tag (`v*`).

### 2. Security and Optimization
- **Env Variables**: Replaced sensitive info in `application.yml` with environment variables.
- **`deploy.sh`**: Optimized the zero-downtime deployment script.

### 3. Documentation
- Updated infrastructure documentation and diagrams.
- Improved the AI Agent work guide.

## Verification
- GitHub Actions workflows verified.
- Deployment scenarios for dev/prod verified.
