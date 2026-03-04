package com.bookreads.ui.home

sealed interface HomeViewState {
    data object Loading : HomeViewState

    data class Ready(
        val username: String,
        val hasActiveSession: Boolean = false,
    ) : HomeViewState
}
