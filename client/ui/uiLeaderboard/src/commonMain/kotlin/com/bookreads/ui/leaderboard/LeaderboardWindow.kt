package com.bookreads.ui.leaderboard

enum class LeaderboardWindow(
    val label: String,
    val apiValue: String,
) {
    Daily("Daily", "daily"),
    Weekly("Weekly", "weekly"),
    Monthly("Monthly", "monthly"),
    AllTime("All-time", "alltime"),
}
