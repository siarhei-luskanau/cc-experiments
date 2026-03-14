package com.bookreads.ui.session

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bookreads.core.common.CoreResult
import com.bookreads.core.common.DispatcherSet
import com.bookreads.core.data.SessionSyncService
import com.bookreads.core.pref.ActiveSession
import com.bookreads.core.pref.LocalSessionStore
import com.bookreads.core.pref.PrefService
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class SessionViewModel(
    private val navigationCallback: SessionNavigationCallback,
    private val prefService: PrefService,
    private val localSessionStore: LocalSessionStore,
    private val sessionSyncService: SessionSyncService,
    private val dispatcherSet: DispatcherSet,
) : ViewModel() {
    val viewState: StateFlow<SessionViewState>
        field = MutableStateFlow<SessionViewState>(SessionViewState.Loading)

    private var elapsedSec: Long = 0L
    private var timerJob: Job? = null

    init {
        viewModelScope.launch(dispatcherSet.defaultDispatcher()) {
            val username = prefService.getKey().first() ?: ""
            val activeSession = localSessionStore.observe().first()
            when {
                activeSession?.pendingStop != null -> {
                    viewState.value = SessionViewState.Idle(username = username)
                    val result = sessionSyncService.retryPendingStop()
                    if (result is CoreResult.Success) {
                        localSessionStore.clear()
                    }
                }

                activeSession != null -> {
                    elapsedSec = activeSession.elapsedOffsetSec
                    viewState.value =
                        SessionViewState.Reading(
                            username = username,
                            bookTitle = activeSession.bookTitle,
                            elapsedSec = elapsedSec,
                        )
                    startTimerLoop()
                    sessionSyncService.startPeriodicSync(viewModelScope) { elapsedSec }
                }

                else -> {
                    viewState.value = SessionViewState.Idle(username = username)
                }
            }
        }
    }

    fun onEvent(event: SessionViewEvent) {
        when (event) {
            SessionViewEvent.StartReadingClicked -> {
                val current = viewState.value
                if (current is SessionViewState.Idle) {
                    viewState.value =
                        SessionViewState.EnteringBook(
                            username = current.username,
                            bookTitle = "",
                        )
                }
            }

            is SessionViewEvent.BookTitleChanged -> {
                val current = viewState.value
                if (current is SessionViewState.EnteringBook) {
                    viewState.value = current.copy(bookTitle = event.title)
                }
            }

            SessionViewEvent.ConfirmBookTitle -> {
                val current = viewState.value
                if (current is SessionViewState.EnteringBook && current.bookTitle.isNotBlank()) {
                    startSession(current.username, current.bookTitle.trim())
                }
            }

            SessionViewEvent.CancelBookTitle -> {
                val current = viewState.value
                if (current is SessionViewState.EnteringBook) {
                    viewState.value = SessionViewState.Idle(username = current.username)
                }
            }

            SessionViewEvent.StopReading -> {
                stopSession()
            }

            SessionViewEvent.GoLeaderboard -> {
                navigationCallback.goLeaderboard()
            }

            SessionViewEvent.GoHome -> {
                navigationCallback.goHome()
            }
        }
    }

    @OptIn(ExperimentalUuidApi::class, ExperimentalTime::class)
    private fun startSession(
        username: String,
        bookTitle: String,
    ) {
        val clientId = Uuid.random().toString()
        val startedAt = Clock.System.now().toString()
        elapsedSec = 0L
        val session =
            ActiveSession(
                clientId = clientId,
                username = username,
                bookTitle = bookTitle,
                startedAt = startedAt,
                elapsedOffsetSec = 0L,
            )
        viewModelScope.launch(dispatcherSet.defaultDispatcher()) {
            localSessionStore.save(session)
            viewState.value =
                SessionViewState.Reading(
                    username = username,
                    bookTitle = bookTitle,
                    elapsedSec = 0L,
                )
            val syncResult = sessionSyncService.syncOnce(durationSec = 0L)
            if (syncResult is CoreResult.Failure) {
                val current = viewState.value
                if (current is SessionViewState.Reading) {
                    viewState.value = current.copy(syncError = true)
                }
            }
            startTimerLoop()
            sessionSyncService.startPeriodicSync(viewModelScope) { elapsedSec }
        }
    }

    @OptIn(ExperimentalTime::class)
    private fun stopSession() {
        timerJob?.cancel()
        timerJob = null
        sessionSyncService.stopPeriodicSync()
        val current = viewState.value
        if (current is SessionViewState.Reading) {
            val finalElapsed = elapsedSec
            val endedAt = Clock.System.now().toString()
            viewModelScope.launch(dispatcherSet.defaultDispatcher()) {
                val result = sessionSyncService.syncOnce(durationSec = finalElapsed, endedAt = endedAt)
                if (result is CoreResult.Failure) {
                    localSessionStore.updatePendingStop(finalElapsed, endedAt)
                } else {
                    localSessionStore.clear()
                }
                viewState.value = SessionViewState.Idle(username = current.username)
            }
        }
    }

    private fun startTimerLoop() {
        timerJob?.cancel()
        timerJob =
            viewModelScope.launch(dispatcherSet.defaultDispatcher()) {
                while (isActive) {
                    delay(1_000L)
                    elapsedSec++
                    val current = viewState.value
                    if (current is SessionViewState.Reading) {
                        viewState.value = current.copy(elapsedSec = elapsedSec)
                    }
                }
            }
    }

    override fun onCleared() {
        timerJob?.cancel()
        sessionSyncService.stopPeriodicSync()
        super.onCleared()
    }
}
