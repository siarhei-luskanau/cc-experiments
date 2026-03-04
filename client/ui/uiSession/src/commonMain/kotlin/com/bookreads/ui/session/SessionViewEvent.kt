package com.bookreads.ui.session

sealed interface SessionViewEvent {
    data object StartReadingClicked : SessionViewEvent

    data class BookTitleChanged(
        val title: String,
    ) : SessionViewEvent

    data object ConfirmBookTitle : SessionViewEvent

    data object CancelBookTitle : SessionViewEvent

    data object StopReading : SessionViewEvent

    data object GoLeaderboard : SessionViewEvent

    data object GoHome : SessionViewEvent
}
