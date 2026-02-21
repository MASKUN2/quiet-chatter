# Application Architecture Guide

This project is built using **Hexagonal Architecture (Ports and Adapters)**.
The goal is to keep the business logic pure and separate it from external technical changes.

## Core Principle: Dependency Rule

The dependency arrows in the source code always flow in this direction: `Adapter -> Application -> Domain`.

- **Domain Layer**: Contains Entities, Value Objects (VO), and domain logic.
- **Application Layer**: Handles Use Cases. It combines domain logic into a use case and manages transactions. It defines **Ports** (interfaces) to talk to the outside world.
- **Adapter Layer**: Connects the application to the outside world. It is divided into **Inbound** and **Outbound** Adapters.

---

## Implementation Guide

### Domain Packages

Each domain has its own top-level package. Inside each package, the code is organized by architecture layers.

### Use Package-Private Access

Use `default` (package-private) access modifiers when possible to keep the design clean. If you need to reference something from outside (like in a test), use Dependency Injection (DI) through interfaces.

### Spring Data JPA in Domain Objects

For convenience, we allow a limited dependency on Spring Data JPA within Domain Entities and VOs.

### Returning Entities from Application Layer

The application layer returns Entities. This is done so the application layer doesn't depend on the adapter layer's concerns. Since it happens outside the transaction boundary, it is usually fine even if domain logic is exposed. If the returned information is a domain object itself (like for statistics), return that object.
