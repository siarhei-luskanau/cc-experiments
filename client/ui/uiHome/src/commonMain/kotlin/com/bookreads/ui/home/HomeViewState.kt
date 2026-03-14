package com.bookreads.ui.home

sealed interface HomeViewState {
    data object Loading : HomeViewState

    data class Ready(
        val username: String,
        val hasActiveSession: Boolean = false,
        val error: String? = null,
        val isLoading: Boolean = false,
    ) : HomeViewState
}
