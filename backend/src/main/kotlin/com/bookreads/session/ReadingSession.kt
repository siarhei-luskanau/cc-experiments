package com.bookreads.session

import com.bookreads.user.User
import jakarta.persistence.*
import java.time.Instant

@Entity
@Table(name = "reading_sessions")
class ReadingSession(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,

    @Column(name = "book_title", nullable = false)
    val bookTitle: String,

    @Column(name = "started_at", nullable = false, insertable = false, updatable = false)
    val startedAt: Instant = Instant.now(),

    @Column(name = "ended_at")
    var endedAt: Instant? = null,

    @Column(name = "duration_sec", insertable = false, updatable = false)
    val durationSec: Long? = null
)
