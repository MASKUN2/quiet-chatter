# Extracted Software Architecture Guide

This guide explains the logical design (**Hexagonal Architecture**) of our backend project. For details regarding physical deployment and CI/CD pipelines, please read `infrastructure_deployment_guide.md`.

## 1. Logical Architecture (Hexagonal)

We use the **Ports and Adapters** pattern, also known as Hexagonal Architecture. This pattern isolates our core business logic from external frameworks, databases, and APIs.

### 1.1 Core Principle: The Dependency Rule

Dependencies must always point **inward** toward the core domain. The layers are structured as follows: `Adapter -> Application -> Domain`.

1. **Domain Layer (Core)**: This layer holds our pure business logic, including Entities and Value Objects (VOs). It must not contain framework dependencies. *Note: We allow minimal JPA annotations here for practical reasons.*
2. **Application Layer (Use Cases)**: This layer coordinates domain objects to perform tasks. It defines **Ports** (Java Interfaces) that explain how external components can communicate with the application.
3. **Adapter Layer (Infrastructure)**: This layer contains the technical details for communicating with the outside world.
    - **Inbound Adapters**: Components that call the application (e.g., Web Controllers, Scheduled Tasks).
    - **Outbound Adapters**: Components called by the application (e.g., JPA Repositories, Redis Clients, External API Clients).

### 1.2 Package Structure

We divide the code into feature modules (e.g., `member`, `book`, `talk`). Each module is independent.

- **Encapsulation**: Use `package-private` (default) access modifiers to hide internal classes.
- **Interfaces**: Expose public functions only through clearly defined interfaces.
