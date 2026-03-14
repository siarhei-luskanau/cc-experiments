package com.bookreads.core.pref

import kotlinx.coroutines.flow.Flow

interface LocalSessionStore {
    fun observe(): Flow<ActiveSession?>

    suspend fun save(session: ActiveSession)

    suspend fun clear()

    suspend fun updatePendingStop(
        durationSec: Long,
        endedAt: String,
    )
}
