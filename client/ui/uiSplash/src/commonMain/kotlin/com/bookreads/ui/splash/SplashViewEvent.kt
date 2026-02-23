package com.bookreads.ui.splash

sealed interface SplashViewEvent {
    data object Launched : SplashViewEvent
}
