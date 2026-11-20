# Chat Service

[![CI](https://github.com/sravani551/ChatApp/actions/workflows/ci.yml/badge.svg)](https://github.com/sravani551/ChatApp/actions/workflows/ci.yml)
![Java](https://img.shields.io/badge/Java-17-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5-6DB33F)

A REST API for one-to-one messaging: user accounts, conversations, messages, read receipts and
unread counts. Built with Spring Boot, Spring Data JPA and Bean Validation.

## API

| Method | Path | Description | Success |
|---|---|---|---|
| `POST` | `/api/users` | Sign up `{username, password}` | 201 |
| `POST` | `/api/auth/login` | Verify credentials | 200 |
| `GET` | `/api/users/{id}` | Get a user | 200 |
| `DELETE` | `/api/users/{id}` | Delete a user | 204 |
| `GET` | `/api/users/{id}/chats` | A user's conversations, newest first | 200 |
| `GET` | `/api/users/{id}/unread-count` | Unread messages addressed to the user | 200 |
| `POST` | `/api/chats` | Start a chat `{userId, otherUserId}` | 201 |
| `DELETE` | `/api/chats/{id}` | Delete a chat and its messages | 204 |
| `POST` | `/api/chats/{id}/messages` | Send `{senderId, content}` | 201 |
| `GET` | `/api/chats/{id}/messages?readerId=&page=&size=` | Read a page of the conversation | 200 |

Errors use [RFC 9457 problem details](https://www.rfc-editor.org/rfc/rfc9457) with meaningful statuses:
400 (validation, with per-field messages), 401 (bad credentials), 403 (not a participant),
404 (unknown user or chat) and 409 (duplicate username or chat).

## Design notes

- **Passwords** are hashed with BCrypt and never returned: responses use DTOs (Java records), never
  JPA entities. Login returns the same 401 for an unknown user and a wrong password, so usernames
  can't be enumerated.
- **One chat per pair of users**: participants are stored in canonical order (lower id first) under a
  unique constraint, so A→B and B→A resolve to the same conversation, enforced by the database
  and not only by application code.
- **Correct read receipts**: opening a chat marks only messages *addressed to the reader* as read
  (`readAt` timestamp), not the reader's own messages.
- **Authorization rule**: only participants can send to or read a chat.
- **Indexes** on `(chat_id, sent_at)` for paging a conversation and `(receiver_id, read_at)` for
  unread counts; reads are paginated (max 200 per page).
- `open-in-view` disabled, read-only transactions for queries, constructor injection throughout.

## Run

```bash
./mvnw spring-boot:run            # http://localhost:8080, in-memory H2
# or
docker build -t chat-service . && docker run -p 8080:8080 chat-service
```

Point it at a real database with `DB_URL`, `DB_USER` and `DB_PASSWORD`; set `H2_CONSOLE=true` to
enable the H2 console during development.

```bash
curl -s -XPOST localhost:8080/api/users -H 'Content-Type: application/json' \
     -d '{"username":"alice","password":"s3cret-pass"}'
curl -s -XPOST localhost:8080/api/users -H 'Content-Type: application/json' \
     -d '{"username":"bob","password":"s3cret-pass"}'
curl -s -XPOST localhost:8080/api/chats -H 'Content-Type: application/json' \
     -d '{"userId":1,"otherUserId":2}'
curl -s -XPOST localhost:8080/api/chats/1/messages -H 'Content-Type: application/json' \
     -d '{"senderId":1,"content":"Hi Bob!"}'
curl -s "localhost:8080/api/chats/1/messages?readerId=2"
curl -s localhost:8080/api/users/2/unread-count
```

## Tests

```bash
./mvnw verify
```

End-to-end API tests (MockMvc against the full Spring context and H2) cover sign-up/login, validation,
conflicts, authorization, read receipts and deletion, plus unit tests for the domain rules.

## Next steps

- Token-based authentication (Spring Security + JWT) so the caller's identity comes from the token
  instead of `senderId`/`readerId` parameters
- Real-time delivery over WebSocket/STOMP
- Flyway migrations and PostgreSQL in place of `ddl-auto`
