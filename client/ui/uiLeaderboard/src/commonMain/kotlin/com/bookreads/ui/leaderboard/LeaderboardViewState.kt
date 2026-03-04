package com.bookreads.ui.leaderboard

import com.bookreads.core.network.api.LeaderboardEntryModel

sealed interface LeaderboardViewState {
    data object Loading : LeaderboardViewState

    data class Loaded(
        val entries: List<LeaderboardEntryModel>,
        val selectedWindow: LeaderboardWindow,
        val currentUsername: String?,
    ) : LeaderboardViewState

    data class Error(
        val error: Throwable,
    ) : LeaderboardViewState
}
