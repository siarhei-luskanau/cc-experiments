package com.bookreads.dto

import kotlinx.serialization.Serializable

@Serializable
data class LeaderboardEntryDto(
    val rank: Int,
    val username: String,
    val totalSec: Long,
    val sessionCount: Int
)

@Serializable
data class LeaderboardUpdateMessage(
    val window: String,
    val entries: List<LeaderboardEntryDto>
)
