# Commit Message Convention

This document introduces rules based on the [AngularJS Git Commit Message Conventions](https://gist.github.com/stephenparish/9941e89d80e2bc58a153). By following these conventions, we keep our project history clean and readable.

## Format Structure

Your commit messages should follow this exact template:

```text
<type>(<scope>): <subject>

<body>

<footer>
```

> **Note**: Try to keep each line under 100 characters to ensure the history is easy to read in the terminal and on GitHub.

## 1. Subject Line

The subject line is a short summary of your change.

### `<type>` (Required)

You must use one of the following words:

- `feat`: Adding a new feature.
- `fix`: Fixing a bug.
- `docs`: Changing documentation (e.g., `README.md`, `guide` files).
- `style`: Formatting code (e.g., missing semicolons, spacing). No logic is changed.
- `refactor`: Restructuring or rewriting code without changing its external behavior.
- `test`: Adding missing tests or fixing existing tests.
- `chore`: Updating build tasks, configuration files, or dependencies.

### `<scope>` (Optional)

Specify the part of the code you modified inside parentheses.
Example scopes: `auth`, `logging`, `Member`, `PaymentService`.

### `<subject>` (Required)

Explain exactly what was done in clear English.

- **Imperative Mood**: Use the present tense imperative. Write "change" or "add", not "changed", "changes", or "added".
- **Lowercase**: Start the subject with a lowercase letter.
- **No Period**: Do not place a period (`.`) at the end of the subject line.

## 2. Body

If the subject line is not enough, add a detailed body to explain the commit.

- Use the present tense imperative, just like the subject line.
- Explain **why** you are making the change and **what is different** from the previous behavior.

## 3. Footer

Use the footer for special references.

### Breaking Changes

If your commit breaks backward compatibility, write a `BREAKING CHANGE:` block in the footer.
You must describe exactly what changed, why it changed, and provide a guide on how to migrate to the new code.

### Referencing Issues

If your commit resolves an issue tracked in GitHub or Jira, use the `Closes` keyword on a separate line.
- Example: `Closes #123`
