package com.bookreads.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bookreads.core.common.CoreResult
import com.bookreads.core.common.DispatcherSet
import com.bookreads.core.data.UserRepository
import com.bookreads.core.pref.LocalSessionStore
import com.bookreads.core.pref.PrefService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class HomeViewModel(
    private val navigationCallback: HomeNavigationCallback,
    private val prefService: PrefService,
    private val localSessionStore: LocalSessionStore,
    private val userRepository: UserRepository,
    private val dispatcherSet: DispatcherSet,
) : ViewModel() {
    val viewState: StateFlow<HomeViewState>
        field = MutableStateFlow<HomeViewState>(HomeViewState.Loading)

    init {
        viewModelScope.launch(dispatcherSet.defaultDispatcher()) {
            combine(
                prefService.getKey(),
                localSessionStore.observe(),
            ) { username, activeSession ->
                HomeViewState.Ready(
                    username = username ?: "",
                    hasActiveSession = activeSession != null,
                )
            }.collect { state ->
                viewState.value = state
            }
        }
    }

    fun onEvent(event: HomeViewEvent) {
        when (event) {
            is HomeViewEvent.UsernameChanged -> {
                val current = viewState.value
                if (current is HomeViewState.Ready) {
                    viewState.value = current.copy(username = event.value)
                }
            }

            HomeViewEvent.EnterPressed -> {
                val current = viewState.value
                if (current is HomeViewState.Ready && current.username.isNotBlank() && !current.isLoading) {
                    viewModelScope.launch(dispatcherSet.defaultDispatcher()) {
                        viewState.value = current.copy(isLoading = true, error = null)
                        val username = current.username.trim()
                        val result = userRepository.registerOrGetUser(username)
                        prefService.setKey(username)
                        if (result is CoreResult.Failure<*>) {
                            viewState.value =
                                current.copy(isLoading = false, error = "Server unreachable. Proceeding offline.")
                        }
                        navigationCallback.goSession()
                    }
                }
            }
        }
    }
}
