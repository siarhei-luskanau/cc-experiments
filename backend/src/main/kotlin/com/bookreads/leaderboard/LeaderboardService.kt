package com.bookreads.leaderboard

import com.bookreads.dto.LeaderboardEntryDto
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.time.temporal.ChronoUnit

@Service
class LeaderboardService(
    private val leaderboardRepository: LeaderboardRepository,
) {
    @Transactional(readOnly = true)
    fun getLeaderboard(window: String): List<LeaderboardEntryDto> {
        val windowStart =
            when (window.lowercase()) {
                "daily" -> Instant.now().truncatedTo(ChronoUnit.DAYS)

                "weekly" -> Instant.now().minus(7, ChronoUnit.DAYS)

                "monthly" -> Instant.now().minus(30, ChronoUnit.DAYS)

                "alltime" -> Instant.EPOCH

                else -> throw IllegalArgumentException(
                    "Invalid window: $window. Use daily, weekly, monthly, or alltime",
                )
            }
        return leaderboardRepository
            .findLeaderboard(windowStart)
            .mapIndexed { index, row ->
                LeaderboardEntryDto(
                    rank = index + 1,
                    username = row.username,
                    totalSec = row.totalSec,
                    sessionCount = row.sessionCount,
                )
            }
    }
}
