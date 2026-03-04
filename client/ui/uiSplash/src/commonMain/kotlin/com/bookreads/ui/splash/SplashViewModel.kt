package com.bookreads.ui.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bookreads.core.common.DispatcherSet
import com.bookreads.core.pref.PrefService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SplashViewModel(
    private val navigationCallback: SplashNavigationCallback,
    private val prefService: PrefService,
    private val dispatcherSet: DispatcherSet,
) : ViewModel() {
    val viewState: StateFlow<SplashViewState>
        field = MutableStateFlow<SplashViewState>(SplashViewState.Loading)

    fun onEvent(event: SplashViewEvent) {
        when (event) {
            SplashViewEvent.Launched -> {
                viewModelScope.launch(dispatcherSet.defaultDispatcher()) {
                    val username = prefService.getKey().first()
                    if (username.isNullOrBlank()) {
                        navigationCallback.goHome()
                    } else {
                        navigationCallback.goSession()
                    }
                }
            }
        }
    }
}
