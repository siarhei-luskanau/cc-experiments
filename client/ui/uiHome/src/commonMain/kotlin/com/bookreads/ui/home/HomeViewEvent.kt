package com.bookreads.ui.home

sealed interface HomeViewEvent {
    data class UsernameChanged(
        val value: String,
    ) : HomeViewEvent

    data object EnterPressed : HomeViewEvent
}
