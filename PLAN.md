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
| Session tracking | Client-side (device clock + local state) |
| Backend role | Sync & leaderboard storage only |
| Leaderboard scope | Global |
| Leaderboard windows | Daily / Weekly / Monthly / All-time |
| Leaderboard refresh | REST polling (on tab focus + periodic) |
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
│  │  Serialization                                 │  │
│  │                                                │  │
│  │  ┌──────────────────────────────────────────┐  │  │
│  │  │         Session Engine (client-owned)    │  │  │
│  │  │  Local timer · Start/stop control        │  │  │
│  │  │  Persist state · Periodic sync           │  │  │
│  │  └──────────────────────────────────────────┘  │  │
│  └────────────────────────────────────────────────┘  │
└──────────────────────┬──────────────────────────────┘
                       │ HTTP REST
┌──────────────────────▼──────────────────────────────┐
│           Spring Boot Backend (Kotlin)               │
│                                                      │
│  Session sync (upsert) · Leaderboard queries         │
│  No session lifecycle logic                          │
│                                                      │
└──────────────────────┬──────────────────────────────┘
                       │ JDBC
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
│           │   └── LeaderboardService.kt
│           └── config/
│               └── SecurityConfig.kt
│
├── client/                           # KMP Gradle module
│   ├── build.gradle.kts
│   └── src/
│       ├── commonMain/kotlin/
│       │   ├── api/
│       │   │   └── BookApi.kt        # Ktor HTTP client
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
    client_id    TEXT NOT NULL UNIQUE,   -- client-generated UUID, used for upsert
    user_id      BIGINT NOT NULL REFERENCES users(id),
    book_title   TEXT NOT NULL,
    started_at   TIMESTAMPTZ NOT NULL,   -- client-provided timestamp
    ended_at     TIMESTAMPTZ,            -- client-provided; NULL = session still active
    duration_sec BIGINT NOT NULL         -- client-computed and sent on every sync
);

CREATE INDEX idx_sessions_user    ON reading_sessions(user_id);
CREATE INDEX idx_sessions_started ON reading_sessions(started_at);
CREATE INDEX idx_sessions_client  ON reading_sessions(client_id);
```

`client_id` is a UUID generated by the client when the session starts. The backend upserts on `client_id`, so repeated syncs (progress updates) update the same row. `duration_sec` is always client-computed — the server never derives it. Leaderboard queries use `WHERE started_at >= :windowStart` on the stored values.

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
| `POST` | `/sessions/sync` | `SessionSyncRequest` | `SessionDto` | Upsert a session by `clientId`; called on start, periodically while active, and on end |
| `GET` | `/sessions/{clientId}` | — | `SessionDto?` | Fetch last-synced state for a session (used on app restore) |

### Leaderboard

| Method | Path | Params | Response | Description |
|---|---|---|---|---|
| `GET` | `/leaderboard` | `window=daily\|weekly\|monthly\|alltime` | `List<LeaderboardEntryDto>` | Ranked list of users by total reading seconds |

---

## DTO Definitions (`shared-dto`)

```kotlin
@Serializable
data class UserDto(val id: Long, val username: String, val createdAt: String)

// Sent by the client on every sync (start, periodic progress, end)
@Serializable
data class SessionSyncRequest(
    val clientId: String,        // client-generated UUID, stable for the session's lifetime
    val username: String,
    val bookTitle: String,
    val startedAt: String,       // ISO-8601, client clock
    val endedAt: String?,        // null while active
    val durationSec: Long        // client-computed elapsed seconds
)

@Serializable
data class SessionDto(
    val clientId: String,
    val username: String,
    val bookTitle: String,
    val startedAt: String,
    val endedAt: String?,
    val durationSec: Long
)

@Serializable
data class LeaderboardEntryDto(
    val rank: Int,
    val username: String,
    val totalSec: Long,
    val sessionCount: Int
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
- Timer runs entirely on the device — no network call needed to tick
- "Start Reading" button → input book title → generates `clientId` UUID, starts local timer, persists session to local storage, fires first sync (`POST /sessions/sync`)
- "Stop Reading" button (visible when session active) → stops local timer, sets `endedAt`, fires final sync with `endedAt` populated, clears active-session local state
- Periodic background sync every 30 s while a session is active (keeps leaderboard current)
- Link to Leaderboard screen

### 3. Leaderboard Screen
- Tab bar: Daily / Weekly / Monthly / All-time
- Ranked list: `#rank · username · Xh Ym` (formatted from totalSec)
- Auto-refresh via REST polling (on tab focus and every 30 s while visible)
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
- [ ] Flyway migration: `users` + `reading_sessions` tables (with `client_id` unique column)
- [ ] `UserController` + `UserService` + `UserRepository`
- [ ] `SessionController` + `SessionService` (`POST /sessions/sync` upsert on `client_id`, `GET /sessions/{clientId}`)
- [ ] `LeaderboardController` + `LeaderboardService` (4 time windows, `SUM(duration_sec)`)
- [ ] Integration tests (Testcontainers + PostgreSQL)

### Phase 2 — KMP Client Scaffold
- [ ] `client` module with all 4 targets (android, iosArm64/iosX64, jvm, wasmJs)
- [ ] Ktor HTTP client wired to backend
- [ ] `expect/actual` local storage interface (SharedPreferences / NSUserDefaults / Preferences / localStorage)
- [ ] `LocalSessionStore` — persists active session state (clientId, bookTitle, startedAt epoch, elapsed offset) across app restarts
- [ ] `SessionSyncService` — fires `POST /sessions/sync`; retries on failure; schedules periodic 30 s sync while active
- [ ] `UserRepository`, `SessionRepository` (client-side), `LeaderboardRepository` (client-side)
- [ ] Koin modules

### Phase 3 — Compose Multiplatform UI
- [ ] `HomeScreen` with username entry
- [ ] `SessionScreen`: device-local timer via `LaunchedEffect` coroutine ticking every second; start generates clientId + fires first sync; stop fires final sync; timer survives recomposition via `StateFlow`
- [ ] `LeaderboardScreen` with 4-tab view; polls `GET /leaderboard` on tab focus and every 30 s while visible
- [ ] Navigation between screens

### Phase 4 — Platform Entry Points
- [ ] Android `MainActivity`
- [ ] iOS `MainViewController` + Xcode project skeleton
- [ ] JVM Desktop `main()` with `singleWindowApplication`
- [ ] WasmJS `main()` with `CanvasBasedWindow`

### Phase 5 — Polish & Packaging
- [ ] Dockerfile for backend (`./gradlew bootJar` → slim JRE image)
- [ ] Error handling: duplicate username, server unreachable (sync queued locally)
- [ ] Sync retry queue: if `POST /sessions/sync` fails, store pending sync in local storage and retry on next connectivity event
- [ ] README with setup and run instructions

---

## Key Design Decisions

**Client owns session lifecycle** — the device starts the timer, owns the `clientId` UUID, and decides when a session ends. No network call is required to begin or resume reading. This means the app works offline; accumulated time is synced when connectivity returns.

**One active session constraint** — enforced on the client. The client stores at most one active session in local storage. The backend does not check for conflicting active sessions; it blindly upserts on `clientId`.

**Sync strategy** — the client fires `POST /sessions/sync` on start, every 30 s while active (progress update), and immediately on end. This keeps leaderboard data reasonably current for active sessions without requiring a persistent connection for the timer itself.

**`duration_sec` is client-authoritative** — the server stores whatever the client sends. The generated-column approach is dropped; `duration_sec` is a plain `BIGINT` column updated on each upsert. This avoids timezone or rounding mismatches between client and server clocks.

**Leaderboard computation** — computed on read (`SUM(duration_sec)` with `WHERE started_at >= windowStart`), not materialized. Active sessions contribute their last-synced `duration_sec`, so the leaderboard is accurate to within the sync interval.

**Leaderboard polling** — the client polls `GET /leaderboard` when the leaderboard tab becomes visible and every 30 s while it remains on screen. No persistent connection is required; results are accurate to within the polling interval (same as the session sync interval).

**Username as identity** — username is stored in a Kotlin `StateFlow` in the client ViewModel and persisted to platform-specific local storage (`SharedPreferences` on Android, `NSUserDefaults` on iOS, `Preferences` on JVM, `localStorage` on WasmJS) via a thin `expect/actual` interface.

**Active session persistence** — the current active session (clientId, bookTitle, startedAt, elapsed offset) is persisted to local storage so a device restart or app kill resumes the in-progress session correctly without data loss.

**Shared DTOs** — `shared-dto` uses only `kotlinx.serialization` annotations. On the backend, Spring's `ObjectMapper` is configured to delegate JSON to kotlinx-serialization. On the client, Ktor's content negotiation uses the same serialization.
