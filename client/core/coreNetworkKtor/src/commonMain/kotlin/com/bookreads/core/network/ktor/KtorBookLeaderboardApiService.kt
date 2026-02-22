package com.bookreads.core.network.ktor

import com.bookreads.core.network.api.BookLeaderboardApiService
import com.bookreads.core.network.api.LeaderboardEntryModel
import com.bookreads.core.network.api.NetworkResult
import com.bookreads.core.network.api.SessionModel
import com.bookreads.core.network.api.SessionSyncModel
import com.bookreads.core.network.api.UserModel
import com.bookreads.dto.LeaderboardEntryDto
import com.bookreads.dto.SessionDto
import com.bookreads.dto.SessionSyncRequest
import com.bookreads.dto.UserDto

internal class KtorBookLeaderboardApiService(
    private val client: BookLeaderboardApiClient,
) : BookLeaderboardApiService {
    override suspend fun registerOrGetUser(username: String): NetworkResult<UserModel> =
        runNetworkCatching { client.registerOrGetUser(username).toModel() }

    override suspend fun getUser(username: String): NetworkResult<UserModel> =
        runNetworkCatching { client.getUser(username).toModel() }

    override suspend fun syncSession(request: SessionSyncModel): NetworkResult<SessionModel> =
        runNetworkCatching { client.syncSession(request.toDto()).toModel() }

    override suspend fun getSession(clientId: String): NetworkResult<SessionModel?> =
        runNetworkCatching { client.getSession(clientId)?.toModel() }

    override suspend fun getLeaderboard(window: String): NetworkResult<List<LeaderboardEntryModel>> =
        runNetworkCatching { client.getLeaderboard(window).map { it.toModel() } }

    private suspend fun <T> runNetworkCatching(action: suspend () -> T): NetworkResult<T> =
        runCatching { action() }.fold(
            onSuccess = { NetworkResult.Success(it) },
            onFailure = { NetworkResult.Failure(it) },
        )
}

private fun UserDto.toModel() = UserModel(id = id, username = username, createdAt = createdAt)

private fun SessionDto.toModel() =
    SessionModel(
        clientId = clientId,
        username = username,
        bookTitle = bookTitle,
        startedAt = startedAt,
        endedAt = endedAt,
        durationSec = durationSec,
    )

private fun SessionSyncModel.toDto() =
    SessionSyncRequest(
        clientId = clientId,
        username = username,
        bookTitle = bookTitle,
        startedAt = startedAt,
        endedAt = endedAt,
        durationSec = durationSec,
    )

private fun LeaderboardEntryDto.toModel() =
    LeaderboardEntryModel(
        rank = rank,
        username = username,
        totalSec = totalSec,
        sessionCount = sessionCount,
    )
