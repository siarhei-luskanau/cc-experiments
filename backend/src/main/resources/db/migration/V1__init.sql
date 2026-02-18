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

CREATE INDEX idx_sessions_user    ON reading_sessions(user_id);
CREATE INDEX idx_sessions_started ON reading_sessions(started_at);
CREATE INDEX idx_sessions_ended   ON reading_sessions(ended_at);
