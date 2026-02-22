package com.bookreads.core.network.api

interface BookLeaderboardApiService {
    suspend fun registerOrGetUser(username: String): NetworkResult<UserModel>

    suspend fun getUser(username: String): NetworkResult<UserModel>

    suspend fun syncSession(request: SessionSyncModel): NetworkResult<SessionModel>

    suspend fun getSession(clientId: String): NetworkResult<SessionModel?>

    suspend fun getLeaderboard(window: String): NetworkResult<List<LeaderboardEntryModel>>
}
