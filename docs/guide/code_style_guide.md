# Code Style Guide

This document defines the code style and development rules for our project. It follows the **Google Java Style Guide** and **Clean Code Principles**.

## 1. Source File Structure

### Packages and Imports

- Write package statements on a single line. Do not line-break them.
- Use explicit imports (e.g., `java.util.List`). Do not use wildcard imports (`*`).
- **Import Order**:
    1. Static imports
    2. Non-static imports
    *(Separate these two groups with a blank line and sort each group alphabetically.)*
- **No Static Class Imports**: Do not use static imports for entire classes.

### Class Definition

- Write only **one** top-level class per source file.
- Order the members of the class logically (e.g., fields, constructors, public methods, private methods).
- Group overloaded methods together so they appear consecutively.

## 2. Formatting

### Braces

- Always use braces for `if`, `else`, `for`, `do`, and `while` statements, even if the body is only one line.
- Use the **K&R style** (Egyptian brackets):
    - The opening brace `{` stays on the same line as the statement.
    - The closing brace `}` starts on a new line.
    - The closing brace `}` is followed by a new line, unless it is immediately followed by `else`, `catch`, or `finally`.

### Indentation and Line Length

- Use **4 spaces** for block indentation. Do not use tabs.
- Write only one statement per line.
- The maximum line length is **120 characters**.
- If a line is too long, break it and indent the continuation line with **8 spaces** (+8 spaces).

### Whitespace

- **Vertical Spacing**: Use blank lines to divide logical blocks of code (e.g., between methods or variable declarations).
- **Horizontal Spacing**:
    - Add spaces around operators (e.g., `a + b`).
    - Add spaces after commas, colons, and semicolons.
    - Add a space between a keyword and its opening parenthesis (e.g., `if (condition)`).

## 3. Naming Rules

### General Rules

- Use only standard ASCII letters and numbers.
- Do not use prefixes or suffixes like `mName` or `name_`.

### Specific Formats

- **Package Names**: All lowercase, with no underscores (e.g., `com.example.deepspace`).
- **Classes and Interfaces**: Use `UpperCamelCase` (e.g., `ImmutableList`).
- **Methods**: Use `lowerCamelCase` (e.g., `sendMessage`).
- **Constants**: Use `CONSTANT_CASE` (all uppercase letters separated by underscores). This is strictly for `static final` immutable fields.
- **Variables and Parameters**: Use `lowerCamelCase` for fields, local variables, and method parameters.
- **Type Variables (Generics)**: Use a single uppercase letter (e.g., `T`) or a class style name ending with `T` (e.g., `RequestT`).

## 4. Clean Code Practices

### Method Design

- **Single Responsibility**: A method must do exactly one thing.
- **Indentation Limit**: Try to limit indentation to one level inside a method.
- **Arguments Limit**: A method should have no more than 3 parameters.
- **No Else Blocks**: Avoid using `else`. Use the "Early Return" pattern instead to handle special cases or errors early.

### Class and Object Design

- **Small Classes**: Keep classes small and focused. Avoid classes that have more than 3 instance variables.
- **Value Objects**: Wrap primitive values and strings in Value Objects (VO) when they have a specific meaning or domain logic.
- **First-Class Collections**: If a class wraps a collection, it should not have any other member variables.
- **No Getters/Setters in Domain Objects**: Hide internal data. Send conceptual messages to objects instead of extracting their data. (Note: DTOs are an exception and may use getters/setters.)

### Code Quality

- **Null Safety**: Assume parameters and return values are non-null by default. Use `@NullMarked` at the package or class level. Use `@Nullable` explicitly only when handling null is intended.
- **Ternary Operators**: Avoid using ternary operators (`? :`). Write clear `if-else` statements or use early returns instead.
- **Clear Messages**: Write all error and log messages in clear, simple English.
- **Law of Demeter**: A method should not navigate through multiple objects. Use only one dot (`.`) per line when accessing object properties.
- **Override Annotation**: Always add the `@Override` annotation when overriding a method from a parent class or interface.
- **Exception Handling**: Never catch an exception and handle it with an empty block. At minimum, log the error.
- **Static Member Access**: Always access static members through the class name, not through an object instance (e.g., `Foo.staticMethod()`, not `fooInstance.staticMethod()`).

## 5. Javadoc Comments

- **Format**: Use the standard `/** ... */` block syntax.
- **Summary**: The first sentence of the comment should be a short summary of the class or method.
- **Usage**: Write Javadoc comments for all `public` classes, as well as `public` and `protected` members.
