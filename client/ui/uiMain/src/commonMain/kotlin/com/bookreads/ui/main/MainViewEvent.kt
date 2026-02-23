package com.bookreads.ui.main

sealed interface MainViewEvent {
    data object NavigateBack : MainViewEvent
}
