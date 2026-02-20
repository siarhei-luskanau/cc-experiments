package com.bookreads.leaderboard

import com.bookreads.session.ReadingSession
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.Repository
import org.springframework.data.repository.query.Param
import java.time.Instant

interface LeaderboardRepository : Repository<ReadingSession, Long> {
    @Query(
        value = """
            SELECT u.username AS username,
                   SUM(s.duration_sec) AS total_sec,
                   COUNT(s.id) AS session_count
            FROM reading_sessions s
            JOIN users u ON u.id = s.user_id
            WHERE s.started_at >= :windowStart
            GROUP BY u.id, u.username
            ORDER BY total_sec DESC
        """,
        nativeQuery = true,
    )
    fun findLeaderboard(
        @Param("windowStart") windowStart: Instant,
    ): List<LeaderboardRow>
}

interface LeaderboardRow {
    val username: String
    val totalSec: Long
    val sessionCount: Int
}
