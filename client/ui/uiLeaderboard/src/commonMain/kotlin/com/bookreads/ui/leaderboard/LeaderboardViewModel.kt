package com.bookreads.ui.leaderboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bookreads.core.common.CoreResult
import com.bookreads.core.common.DispatcherSet
import com.bookreads.core.data.LeaderboardRepository
import com.bookreads.core.pref.PrefService
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class LeaderboardViewModel(
    private val navigationCallback: LeaderboardNavigationCallback,
    private val leaderboardRepository: LeaderboardRepository,
    private val prefService: PrefService,
    private val dispatcherSet: DispatcherSet,
) : ViewModel() {
    val viewState: StateFlow<LeaderboardViewState>
        field = MutableStateFlow<LeaderboardViewState>(LeaderboardViewState.Loading)

    private var pollingJob: Job? = null

    fun onEvent(event: LeaderboardViewEvent) {
        when (event) {
            is LeaderboardViewEvent.TabSelected -> {
                val window = event.window
                val current = viewState.value
                if (current is LeaderboardViewState.Loaded) {
                    viewState.value = current.copy(selectedWindow = window)
                }
                restartPolling(window)
            }

            LeaderboardViewEvent.GoBack -> {
                navigationCallback.goBack()
            }
        }
    }

    fun startPolling() {
        val window =
            (viewState.value as? LeaderboardViewState.Loaded)?.selectedWindow
                ?: LeaderboardWindow.AllTime
        restartPolling(window)
    }

    fun stopPolling() {
        pollingJob?.cancel()
        pollingJob = null
    }

    private fun restartPolling(window: LeaderboardWindow) {
        pollingJob?.cancel()
        pollingJob =
            viewModelScope.launch(dispatcherSet.ioDispatcher()) {
                while (isActive) {
                    loadLeaderboard(window)
                    delay(POLL_INTERVAL_MS)
                }
            }
    }

    private suspend fun loadLeaderboard(window: LeaderboardWindow) {
        val username = prefService.getKey().first()
        when (val result = leaderboardRepository.getLeaderboard(window.apiValue)) {
            is CoreResult.Success -> {
                viewState.value =
                    LeaderboardViewState.Loaded(
                        entries = result.result,
                        selectedWindow = window,
                        currentUsername = username,
                    )
            }

            is CoreResult.Failure -> {
                if (viewState.value is LeaderboardViewState.Loading) {
                    viewState.value = LeaderboardViewState.Error(result.error)
                }
            }
        }
    }

    override fun onCleared() {
        stopPolling()
        super.onCleared()
    }

    companion object {
        private const val POLL_INTERVAL_MS = 30_000L
    }
}
