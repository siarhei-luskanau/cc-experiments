package com.bookreads.core.network.stub

import com.bookreads.core.network.api.BookLeaderboardApiService
import com.bookreads.core.network.api.LeaderboardEntryModel
import com.bookreads.core.network.api.NetworkResult
import com.bookreads.core.network.api.SessionModel
import com.bookreads.core.network.api.SessionSyncModel
import com.bookreads.core.network.api.UserModel

internal class StubBookLeaderboardApiService : BookLeaderboardApiService {
    override suspend fun registerOrGetUser(username: String): NetworkResult<UserModel> =
        NetworkResult.Success(stubUser(username))

    override suspend fun getUser(username: String): NetworkResult<UserModel> = NetworkResult.Success(stubUser(username))

    override suspend fun syncSession(request: SessionSyncModel): NetworkResult<SessionModel> =
        NetworkResult.Success(
            SessionModel(
                clientId = request.clientId,
                username = request.username,
                bookTitle = request.bookTitle,
                startedAt = request.startedAt,
                endedAt = request.endedAt,
                durationSec = request.durationSec,
            ),
        )

    override suspend fun getSession(clientId: String): NetworkResult<SessionModel?> = NetworkResult.Success(null)

    override suspend fun getLeaderboard(window: String): NetworkResult<List<LeaderboardEntryModel>> =
        NetworkResult.Success(stubLeaderboard)

    private fun stubUser(username: String) =
        UserModel(
            id = 1L,
            username = username,
            createdAt = "2024-01-01T00:00:00Z",
        )

    private val stubLeaderboard =
        listOf(
            LeaderboardEntryModel(rank = 1, username = "alice", totalSec = 7200L, sessionCount = 3),
            LeaderboardEntryModel(rank = 2, username = "bob", totalSec = 5400L, sessionCount = 2),
            LeaderboardEntryModel(rank = 3, username = "carol", totalSec = 3600L, sessionCount = 1),
        )
}
