package com.bookreads.ui.session

sealed interface SessionViewState {
    data object Loading : SessionViewState

    data class Idle(
        val username: String,
    ) : SessionViewState

    data class EnteringBook(
        val username: String,
        val bookTitle: String,
    ) : SessionViewState

    data class Reading(
        val username: String,
        val bookTitle: String,
        val elapsedSec: Long,
    ) : SessionViewState
}
