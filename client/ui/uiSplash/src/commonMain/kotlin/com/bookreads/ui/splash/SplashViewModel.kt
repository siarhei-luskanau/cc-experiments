package com.bookreads.ui.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SplashViewModel(
    private val navigationCallback: SplashNavigationCallback,
) : ViewModel() {
    val viewState: StateFlow<SplashViewState>
        field = MutableStateFlow<SplashViewState>(SplashViewState.Loading)

    fun onEvent(event: SplashViewEvent) {
        when (event) {
            SplashViewEvent.Launched -> {
                viewModelScope.launch {
                    navigationCallback.goMainScreen(initArg = "initArg")
                }
            }
        }
    }
}
