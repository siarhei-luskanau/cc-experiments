# Book Reading Leaderboard — Project Plan

## Overview

A real-time leaderboard application that tracks how much time users spend reading. Users pick a display name, start/stop a single reading session at a time (tagged with a book title), and compete on a shared global leaderboard across daily, weekly, monthly, and all-time windows.

---

## Requirements Summary

| Dimension | Decision |
|---|---|
| Leaderboard metric | Total cumulative reading time |
| Identity | Username only — no auth, no passwords |
| Book entry | Free-text title (session label) |
| Concurrent sessions | One active session per user at a time |
| Leaderboard scope | Global |
| Leaderboard windows | Daily / Weekly / Monthly / All-time |
| Real-time updates | WebSocket (SSE fallback) |
| Backend framework | Spring Boot (Kotlin) |
| Database | PostgreSQL |
| Deployment | Docker Compose (local / self-hosted) |
| Client UI | Compose Multiplatform (shared UI) |
| Client targets | Android, iOS, JVM Desktop, WasmJS |

---

## Architecture

```
┌─────────────────────────────────────────────────────┐
│                  KMP Client (CMP UI)                 │
│  Android │ iOS │ JVM Desktop │ WasmJS (browser)     │
│                                                      │
│  ┌────────────────────────────────────────────────┐  │
│  │           commonMain (shared)                  │  │
│  │  Compose UI · ViewModels · Domain · Ktor HTTP  │  │
│  │  WebSocket client · Serialization              │  │
│  └────────────────────────────────────────────────┘  │
└──────────────────────┬──────────────────────────────┘
                       │ HTTP REST + WebSocket
┌──────────────────────▼──────────────────────────────┐
│           Spring Boot Backend (Kotlin)               │
│                                                      │
│  REST API · WebSocket broadcast · Business logic     │
│                                                      │
└──────────────────────┬──────────────────────────────┘
                       │ JDBC / R2DBC
┌──────────────────────▼──────────────────────────────┐
│                   PostgreSQL                         │
└─────────────────────────────────────────────────────┘
```

---

## Repository Structure (Monorepo)

```
book-leaderboard/
├── backend/                          # Spring Boot Gradle module
│   ├── build.gradle.kts
│   └── src/
│       └── main/kotlin/
│           ├── Application.kt
│           ├── user/
│           │   ├── UserController.kt
│           │   ├── UserService.kt
│           │   └── UserRepository.kt
│           ├── session/
│           │   ├── SessionController.kt
│           │   ├── SessionService.kt
│           │   └── SessionRepository.kt
│           ├── leaderboard/
│           │   ├── LeaderboardController.kt
│           │   ├── LeaderboardService.kt
│           │   └── LeaderboardWebSocketHandler.kt
│           └── config/
│               ├── WebSocketConfig.kt
│               └── SecurityConfig.kt
│
├── client/                           # KMP Gradle module
│   ├── build.gradle.kts
│   └── src/
│       ├── commonMain/kotlin/
│       │   ├── api/
│       │   │   ├── BookApi.kt        # Ktor HTTP client
│       │   │   └── LeaderboardWs.kt  # WebSocket client
│       │   ├── model/
│       │   │   ├── User.kt
│       │   │   ├── Session.kt
│       │   │   └── LeaderboardEntry.kt
│       │   ├── viewmodel/
│       │   │   ├── HomeViewModel.kt
│       │   │   ├── SessionViewModel.kt
│       │   │   └── LeaderboardViewModel.kt
│       │   └── ui/
│       │       ├── App.kt            # Root Compose entry
│       │       ├── HomeScreen.kt
│       │       ├── SessionScreen.kt
│       │       └── LeaderboardScreen.kt
│       ├── androidMain/kotlin/
│       │   └── MainActivity.kt
│       ├── iosMain/kotlin/
│       │   └── MainViewController.kt
│       ├── jvmMain/kotlin/
│       │   └── Main.kt               # Desktop entry
│       └── wasmJsMain/kotlin/
│           └── Main.kt               # Browser entry
│
├── shared-dto/                       # Pure Kotlin/JVM — shared API models
│   ├── build.gradle.kts
│   └── src/main/kotlin/
│       ├── UserDto.kt
│       ├── SessionDto.kt
│       └── LeaderboardDto.kt
│
├── docker-compose.yml
├── settings.gradle.kts
└── build.gradle.kts
```

> `shared-dto` is a plain Kotlin/JVM module consumed by the backend and referenced as source in `commonMain` of the KMP client via `expect/actual`-free data classes with `@Serializable`.

---

## Tech Stack

### Backend
| Layer | Technology |
|---|---|
| Language | Kotlin 2.x |
| Framework | Spring Boot 3.x |
| WebSocket | Spring WebSocket (STOMP over SockJS or raw WS) |
| DB access | Spring Data JPA + Hibernate |
| Connection pool | HikariCP |
| Migrations | Flyway |
| Build | Gradle (Kotlin DSL) |
| Runtime | JVM 21 (virtual threads) |

### Client (KMP)
| Layer | Technology |
|---|---|
| Language | Kotlin 2.x Multiplatform |
| UI | Compose Multiplatform |
| HTTP | Ktor Client (CIO engine on JVM/WasmJS, Darwin on iOS) |
| WebSocket | Ktor WebSocket plugin |
| Serialization | kotlinx.serialization |
| Async | Kotlin Coroutines + Flow |
| DI | Koin Multiplatform |
| Navigation | Compose Navigation (multiplatform) |

### Infrastructure
| Component | Technology |
|---|---|
| Database | PostgreSQL 16 |
| Containerization | Docker + Docker Compose |
| Backend container | Eclipse Temurin 21 slim |

---

## Database Schema

```sql
-- V1__init.sql (Flyway migration)

CREATE TABLE users (
    id         BIGSERIAL PRIMARY KEY,
    username   TEXT NOT NULL UNIQUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE reading_sessions (
    id           BIGSERIAL PRIMARY KEY,
    user_id      BIGINT NOT NULL REFERENCES users(id),
    book_title   TEXT NOT NULL,
    started_at   TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    ended_at     TIMESTAMPTZ,
    duration_sec BIGINT GENERATED ALWAYS AS (
        EXTRACT(EPOCH FROM (ended_at - started_at))::BIGINT
    ) STORED
);

CREATE INDEX idx_sessions_user     ON reading_sessions(user_id);
CREATE INDEX idx_sessions_started  ON reading_sessions(started_at);
CREATE INDEX idx_sessions_ended    ON reading_sessions(ended_at);
```

Leaderboard queries use a parameterized `WHERE started_at >= :windowStart` to compute per-window totals.

---

## REST API

Base path: `/api/v1`

### Users

| Method | Path | Body | Response | Description |
|---|---|---|---|---|
| `POST` | `/users` | `{ "username": "alice" }` | `UserDto` | Register or fetch existing user |
| `GET` | `/users/{username}` | — | `UserDto` | Lookup user |

### Sessions

| Method | Path | Body | Response | Description |
|---|---|---|---|---|
| `POST` | `/sessions/start` | `{ "username": "alice", "bookTitle": "Dune" }` | `SessionDto` | Start reading session; errors if one already active |
| `POST` | `/sessions/end` | `{ "username": "alice" }` | `SessionDto` | End active session; errors if none active |
| `GET` | `/sessions/active/{username}` | — | `SessionDto?` | Get current active session if any |

### Leaderboard

| Method | Path | Params | Response | Description |
|---|---|---|---|---|
| `GET` | `/leaderboard` | `window=daily\|weekly\|monthly\|alltime` | `List<LeaderboardEntryDto>` | Ranked list of users by total reading seconds |

### WebSocket

- **Endpoint**: `ws://host/ws/leaderboard`
- On connect: server pushes current leaderboard snapshot for all windows.
- On any session start/end: server broadcasts updated leaderboard to all connected clients.
- Message format: `LeaderboardUpdateMessage` (JSON, kotlinx.serialization).

---

## DTO Definitions (`shared-dto`)

```kotlin
@Serializable
data class UserDto(val id: Long, val username: String, val createdAt: String)

@Serializable
data class SessionDto(
    val id: Long,
    val username: String,
    val bookTitle: String,
    val startedAt: String,
    val endedAt: String?,
    val durationSec: Long?
)

@Serializable
data class LeaderboardEntryDto(
    val rank: Int,
    val username: String,
    val totalSec: Long,
    val sessionCount: Int
)

@Serializable
data class LeaderboardUpdateMessage(
    val window: String,          // "daily" | "weekly" | "monthly" | "alltime"
    val entries: List<LeaderboardEntryDto>
)
```

---

## Client Screens

### 1. Home Screen
- Text field: enter username
- "Enter" button → stores username locally, navigates to Session screen
- Shows current active session indicator if one is live

### 2. Session Screen
- Displays username and currently active session (book title + elapsed timer)
- "Start Reading" button → input book title → POST `/sessions/start`
- "Stop Reading" button (visible when session active) → POST `/sessions/end`
- Link to Leaderboard screen

### 3. Leaderboard Screen
- Tab bar: Daily / Weekly / Monthly / All-time
- Ranked list: `#rank · username · Xh Ym` (formatted from totalSec)
- Live update via WebSocket — no manual refresh needed
- Highlight current user's row

---

## Docker Compose

```yaml
services:
  db:
    image: postgres:16-alpine
    environment:
      POSTGRES_DB: bookreads
      POSTGRES_USER: bookreads
      POSTGRES_PASSWORD: bookreads
    ports:
      - "5432:5432"
    volumes:
      - pgdata:/var/lib/postgresql/data

  backend:
    build: ./backend
    depends_on:
      - db
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://db:5432/bookreads
      SPRING_DATASOURCE_USERNAME: bookreads
      SPRING_DATASOURCE_PASSWORD: bookreads
    ports:
      - "8080:8080"

volumes:
  pgdata:
```

---

## Implementation Phases

### Phase 1 — Backend Foundation
- [ ] Gradle root project + `backend` and `shared-dto` modules
- [ ] Docker Compose with PostgreSQL
- [ ] Flyway migration: `users` + `reading_sessions` tables
- [ ] `UserController` + `UserService` + `UserRepository`
- [ ] `SessionController` + `SessionService` (start/end logic, one-active constraint)
- [ ] `LeaderboardController` + `LeaderboardService` (4 time windows)
- [ ] Integration tests (Testcontainers + PostgreSQL)

### Phase 2 — Real-time Broadcast
- [ ] Spring WebSocket configuration (raw WS endpoint `/ws/leaderboard`)
- [ ] `LeaderboardWebSocketHandler`: maintain connected sessions, push on session change
- [ ] Unit tests for broadcast logic

### Phase 3 — KMP Client Scaffold
- [ ] `client` module with all 4 targets (android, iosArm64/iosX64, jvm, wasmJs)
- [ ] Ktor HTTP client wired to backend
- [ ] Ktor WebSocket client with reconnection logic
- [ ] `UserRepository`, `SessionRepository`, `LeaderboardRepository` (client-side)
- [ ] Koin modules

### Phase 4 — Compose Multiplatform UI
- [ ] `HomeScreen` with username entry
- [ ] `SessionScreen` with start/stop actions and live elapsed timer
- [ ] `LeaderboardScreen` with 4-tab view and live WebSocket updates
- [ ] Navigation between screens

### Phase 5 — Platform Entry Points
- [ ] Android `MainActivity`
- [ ] iOS `MainViewController` + Xcode project skeleton
- [ ] JVM Desktop `main()` with `singleWindowApplication`
- [ ] WasmJS `main()` with `CanvasBasedWindow`

### Phase 6 — Polish & Packaging
- [ ] Dockerfile for backend (`./gradlew bootJar` → slim JRE image)
- [ ] Error handling: duplicate username, no active session, server unreachable
- [ ] Offline indicator in client when WebSocket disconnects
- [ ] README with setup and run instructions

---

## Key Design Decisions

**One active session constraint** — enforced at the service layer: `start` checks for any row with `ended_at IS NULL` for the user and throws a `409 Conflict` if found.

**Leaderboard computation** — computed on read (SQL `SUM(duration_sec)` with `WHERE started_at >= windowStart`), not materialized. For a small-to-medium user base this is fine. A materialized view or scheduled cache can be added later.

**WebSocket broadcast strategy** — the backend maintains a `CopyOnWriteArraySet<WebSocketSession>`. On any session mutation, the leaderboard service recomputes all 4 windows and broadcasts `LeaderboardUpdateMessage` for each window to all connected clients.

**Username as identity** — username is stored in a Kotlin `StateFlow` in the client ViewModel and persisted to platform-specific local storage (`SharedPreferences` on Android, `NSUserDefaults` on iOS, `Preferences` on JVM, `localStorage` on WasmJS) via a thin `expect/actual` interface.

**Shared DTOs** — `shared-dto` uses only `kotlinx.serialization` annotations. On the backend, Spring's `ObjectMapper` is configured to delegate JSON to kotlinx-serialization. On the client, Ktor's content negotiation uses the same serialization.
