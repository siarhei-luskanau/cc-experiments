package com.bookreads.ui.leaderboard

sealed interface LeaderboardViewEvent {
    data class TabSelected(
        val window: LeaderboardWindow,
    ) : LeaderboardViewEvent

    data object GoBack : LeaderboardViewEvent
}
