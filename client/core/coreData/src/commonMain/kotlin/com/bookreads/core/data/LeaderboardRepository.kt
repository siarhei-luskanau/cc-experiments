package com.bookreads.core.data

import com.bookreads.core.common.CoreResult
import com.bookreads.core.network.api.BookLeaderboardApiService
import com.bookreads.core.network.api.LeaderboardEntryModel

interface LeaderboardRepository {
    suspend fun getLeaderboard(window: String): CoreResult<List<LeaderboardEntryModel>>
}

internal class LeaderboardRepositoryImpl(
    private val apiService: BookLeaderboardApiService,
) : LeaderboardRepository {
    override suspend fun getLeaderboard(window: String): CoreResult<List<LeaderboardEntryModel>> =
        apiService.getLeaderboard(window).toCoreResult()
}
