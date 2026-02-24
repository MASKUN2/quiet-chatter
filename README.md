# Quiet Chatter: You Belong Here

Welcome to **Quiet Chatter**, the backend API server for a book-themed social network (SNS for readers). 

## 📖 Project Overview

This project provides the core functionality for readers to connect, discuss topics, and share book-related content. It is built with a focus on clean design, scalability, and maintainability.

- **Architecture**: Hexagonal Architecture (Ports and Adapters)
- **Primary Framework**: Spring Boot 3.5.x on Java 21
- **Databases**: PostgreSQL (Main DB), Redis (Token & Cache Management)
- **Infrastructure**: AWS LightSail, Docker, Nginx, Watchtower
- **CI/CD**: GitHub Actions

## 📚 Documentation and Guidelines

We maintain extensive documentation to help developers and AI agents understand the project structure and adhere to our coding standards. 

Please refer to the following guides based on your needs:
- **[AGENTS.md](./AGENTS.md)**: The primary entry point for AI Agents. Contains rules and workflow guidelines.
- **[Architecture Guide](./docs/guide/architecture_guide.md)**: Explains the Hexagonal Architecture and package structures.
- **[Infrastructure Guide](./docs/guide/infrastructure_deployment_guide.md)**: Explains the CI/CD pipelines, Docker setup, and staging environments.
- **[API Guide](./docs/guide/api_guide.md)**: Rules for RESTful API design.
- **[Security Guide](./docs/guide/security_guide.md)**: Explains JWT and CORS policies.
- **[Service & Policy Docs](https://github.com/maskun2/quiet-chatter-docs)**: External repository containing detailed project requirements, policies, and history.

## 🚀 Getting Started

If you are an AI assisting with development, **you must read [AGENTS.md](./AGENTS.md)** before starting any work to understand our strict code rules and workflows.
