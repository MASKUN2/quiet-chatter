# Code Style Guide

This document defines the code style and development rules for this project. It is based on the `Google Java Style Guide` and `Clean Code Principles`.

## 1. Source File Structure

### Packages and Imports

- Write package statements on one line without breaks.
- Use explicit imports instead of wildcards (`*`).
- Order of imports:
    1. static imports
    2. non-static imports
    - Separate each group with a blank line and sort them by ASCII order.
- Do not use static imports for classes.

### Class Definition

- Only one top-level class per source file.
- Put class members in a logical order.
- Write overloaded methods together.

## 2. Formatting

### Braces

- Always use braces for `if`, `else`, `for`, `do`, and `while`, even for one line.
- Use the K&R style (Egyptian brackets):
    - Opening brace stays on the same line.
    - Closing brace starts a new line.
    - Closing brace is followed by a new line (except for `else` or `catch`).

### Indents and Line Breaks

- Use +4 spaces for block indentation.
- One statement per line.
- Length limit is 120 characters per line.
- Use +8 spaces indent when breaking a long line.

### Whitespace

- Vertical: Use blank lines to separate logical parts of code.
- Horizontal: Use spaces between keywords and parentheses, around operators, and after commas/colons/semicolons.

## 3. Naming

### Common Rules

- Use only ASCII letters and numbers.
- Do not use prefixes or suffixes (like `mName` or `name_`).

### Specific Rules

- **Package**: All lowercase, no underscores (e.g., `com.example.deepspace`).
- **Class/Interface**: UpperCamelCase (e.g., `ImmutableList`).
- **Method**: lowerCamelCase (e.g., `sendMessage`).
- **Constant**: CONSTANT_CASE (all caps with underscores). Used for `static final` immutable objects.
- **Field/Parameter/Local Variable**: lowerCamelCase.
- **Type Variable**: One uppercase letter (e.g., `T`) or Class style + T (e.g., `RequestT`).

## 4. Clean Code & Practices

### Method Design

- **One Job**: A method should only do one thing.
- **Indent Limit**: Limit indentation to one level per method.
- **Arguments**: No more than 3 arguments per method.
- **No Else**: Avoid using `else`. Use the "Early Return" pattern instead.

### Class and Object Design

- **Small Classes**: Keep classes small. Avoid classes with more than 3 instance variables.
- **Wrapping**: Wrap all primitive values and strings in Value Objects (VO).
- **First-Class Collections**: A class with a collection should not have other member variables.
- **Avoid Getter/Setter**: Do not use getters/setters in domain objects. Send messages to the object instead. (DTOs are an exception.)

### Quality and Maintenance

- **Null Safety**: Use `@NullMarked` to show that parameters and return values are non-null by default. Use `@Nullable` only when null is allowed.
- **Avoid Ternary Operators**: Try not to use them.
- **Error Messages**: Write all error messages and exception messages in simple English.
- **Logging**: Write log messages in English.
- **Law of Demeter**: Use only one dot (.) per line of code.
- **Use @Override**: Always use `@Override` when overriding a method from a parent class.
- **Exception Handling**: Never catch an exception and do nothing.
- **Static Access**: Access static members via the class name (e.g., `Foo.staticMethod()`).

## 5. Javadoc

- **Format**: Use `/** ... */`.
- **Summary**: Start with a short summary in the first sentence.
- **Where to use**: Use for `public` classes and `public`/`protected` members.
