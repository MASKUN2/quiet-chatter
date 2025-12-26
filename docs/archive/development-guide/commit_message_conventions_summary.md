Commit Message Conventions
==========================
origin [AngularJS Git Commit Message Conventions](https://gist.github.com/stephenparish/9941e89d80e2bc58a153)

* it provides better information when browsing the history

Format of the commit message
----------------------------

```
<type>(<scope>): <subject>
<BLANK LINE>
<body>
<BLANK LINE>
<footer>
```

For to be easier to read. Commit message cannot be longer than 100 characters.

### Subject line

The subject line contains a succinct description of the change.

#### Allowed `<type>`

* `feat` feature
* `fix` bug fix
* `docs`  documentation
* `style` formatting, missing semi colons, …
* `refactor`
* `test` when adding missing tests
* `chore` maintain

#### Allowed `<scope>`

Scope could be anything specifying the place of the commit change. For example, auth, logging, Member Entity, etc...

#### `<subject>` text

* use imperative, present tense: “change” not “changed” nor “changes”
* don't capitalize the first letter.
* no dot (.) at the end.

### Message body

* just as in <subject> use imperative, present tense: “change” not “changed” nor “changes”
* includes reason for the change and contrasts with previous behavior

### Message footer

#### Breaking changes

All breaking changes have to be mentioned in footer with the description of the change, justification and migration
notes

#### Referencing issues

Closed bugs should be listed on a separate line in the footer prefixed with the "Closes" keyword.
