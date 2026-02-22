package com.bookreads.core.network.api

data class LeaderboardEntryModel(
    val rank: Int,
    val username: String,
    val totalSec: Long,
    val sessionCount: Int,
)
