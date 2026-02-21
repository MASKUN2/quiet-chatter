# Commit Message Convention

This document is based on the [AngularJS Git Commit Message Conventions](https://gist.github.com/stephenparish/9941e89d80e2bc58a153).

## Format

```
<type>(<scope>): <subject>

<body>

<footer>
```

Keep each line under 100 characters for better readability.

## 1. Subject Line

A short summary of the change.

### `<type>` (Required)

* `feat`: New feature
* `fix`: Bug fix
* `docs`: Documentation changes
* `style`: Formatting, missing semicolons, etc. (No logic changes)
* `refactor`: Code refactoring
* `test`: Adding or fixing tests
* `chore`: Build tasks, package manager configs, etc.

### `<scope>` (Optional)

The location of the change (e.g., `auth`, `logging`, `Member`, `PaymentService`).

### `<subject>` (Required)

* Use the imperative, present tense: "change", not "changed" or "changes".
* Use lowercase for the first letter.
* Do not end with a period (.).

## 2. Body

* Use the imperative, present tense (like the subject).
* Explain **why** you made the change and **how it differs** from previous behavior.

## 3. Footer

### Breaking Changes

All breaking changes must be mentioned in the footer. Include a description of the change, why you did it, and a migration guide.

### Referencing Issues

Link closed bugs or related issues using the `Closes` keyword on a separate line.
Example: `Closes #123`
