package com.bookreads.session

import com.bookreads.user.User
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.time.Instant

@Entity
@Table(name = "reading_sessions")
class ReadingSession(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @Column(name = "client_id", nullable = false, unique = true, updatable = false)
    val clientId: String,
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,
    @Column(name = "book_title", nullable = false)
    val bookTitle: String,
    @Column(name = "started_at", nullable = false)
    val startedAt: Instant,
    @Column(name = "ended_at")
    var endedAt: Instant?,
    @Column(name = "duration_sec", nullable = false)
    var durationSec: Long,
)
