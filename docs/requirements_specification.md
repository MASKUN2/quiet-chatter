# Requirements Specification

## Domain Model

- **Book**: Includes ISBN, title, publisher, author, thumbnail, and description.
- **Member**: Divided into Regular Members (via Naver) and Admin. (Guest role has been removed)
- **Talk**: Content of up to 250 characters. Has a creation time for the auto-hidden policy. Supports "isModified" status based on modification time.
- **Reaction**: User response to a 'Talk' (Like, Support).

## Authentication

- **Naver OAuth2**: Primary login method.
- **JWT Token**: Uses Access Token and Refresh Token (HTTP-only Cookies).
- **Session-less**: Stateless authentication structure.

## Functional Requirements

### Book and Author Management

- Users can search for books using keywords (title, author, etc.).
- Connect with external book APIs (Naver Open API) to manage book information.
- Supports infinite scrolling (Offset-based Slice) for book lists.

### Talk and Community

- Registered members can create, edit, or delete a 'Talk' about a book.
- A 'Talk' can be up to 250 characters.
- Registered members can react (Like, Support) to 'Talks'.
- All users (including anonymous) can view 'Talks' and recommendations.
- Supports infinite scrolling for lists.

## Policies and Permissions

- **Anonymous Users**: Can only perform read operations (Search, View Talks). Writing or reacting requires login.
- **Auto-Hidden Policy**: By default, 'Talks' that are 1 year old are automatically hidden (made private).
- **API Versioning**: All endpoints follow the `/v1/` prefix.
