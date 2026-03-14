package com.bookreads.core.data

import com.bookreads.core.common.CoreResult
import com.bookreads.core.common.DispatcherSet
import com.bookreads.core.network.api.SessionModel
import com.bookreads.core.network.api.SessionSyncModel
import com.bookreads.core.pref.LocalSessionStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class SessionSyncService(
    private val sessionRepository: SessionRepository,
    private val sessionStore: LocalSessionStore,
    private val dispatcherSet: DispatcherSet,
) {
    private var periodicSyncJob: Job? = null

    suspend fun syncOnce(
        durationSec: Long,
        endedAt: String? = null,
    ): CoreResult<SessionModel> {
        val active =
            sessionStore.observe().first()
                ?: return CoreResult.Failure(IllegalStateException("No active session"))
        return syncWithRetry(
            SessionSyncModel(
                clientId = active.clientId,
                username = active.username,
                bookTitle = active.bookTitle,
                startedAt = active.startedAt,
                endedAt = endedAt,
                durationSec = durationSec,
            ),
        )
    }

    fun startPeriodicSync(
        scope: CoroutineScope,
        elapsedSecProvider: () -> Long,
    ) {
        periodicSyncJob?.cancel()
        periodicSyncJob =
            scope.launch(dispatcherSet.ioDispatcher()) {
                while (isActive) {
                    delay(SYNC_INTERVAL_MS)
                    syncOnce(elapsedSecProvider())
                }
            }
    }

    suspend fun retryPendingStop(): CoreResult<SessionModel> {
        val active =
            sessionStore.observe().first()
                ?: return CoreResult.Failure(IllegalStateException("No active session"))
        val pending =
            active.pendingStop
                ?: return CoreResult.Failure(IllegalStateException("No pending stop"))
        return syncWithRetry(
            SessionSyncModel(
                clientId = active.clientId,
                username = active.username,
                bookTitle = active.bookTitle,
                startedAt = active.startedAt,
                endedAt = pending.endedAt,
                durationSec = pending.durationSec,
            ),
        )
    }

    fun stopPeriodicSync() {
        periodicSyncJob?.cancel()
        periodicSyncJob = null
    }

    private suspend fun syncWithRetry(
        request: SessionSyncModel,
        maxAttempts: Int = 3,
    ): CoreResult<SessionModel> {
        var lastResult: CoreResult<SessionModel> = CoreResult.Failure(IllegalStateException("Not attempted"))
        repeat(maxAttempts) { attempt ->
            lastResult = sessionRepository.syncSession(request)
            if (lastResult is CoreResult.Success) return lastResult
            if (attempt < maxAttempts - 1) delay(RETRY_DELAY_MS)
        }
        return lastResult
    }

    companion object {
        private const val SYNC_INTERVAL_MS = 30_000L
        private const val RETRY_DELAY_MS = 1_000L
    }
}
