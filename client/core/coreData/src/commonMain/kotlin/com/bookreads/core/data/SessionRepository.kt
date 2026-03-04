package com.bookreads.core.data

import com.bookreads.core.common.CoreResult
import com.bookreads.core.network.api.BookLeaderboardApiService
import com.bookreads.core.network.api.SessionModel
import com.bookreads.core.network.api.SessionSyncModel

interface SessionRepository {
    suspend fun syncSession(request: SessionSyncModel): CoreResult<SessionModel>

    suspend fun getSession(clientId: String): CoreResult<SessionModel?>
}

internal class SessionRepositoryImpl(
    private val apiService: BookLeaderboardApiService,
) : SessionRepository {
    override suspend fun syncSession(request: SessionSyncModel): CoreResult<SessionModel> =
        apiService.syncSession(request).toCoreResult()

    override suspend fun getSession(clientId: String): CoreResult<SessionModel?> =
        apiService.getSession(clientId).toCoreResult()
}
