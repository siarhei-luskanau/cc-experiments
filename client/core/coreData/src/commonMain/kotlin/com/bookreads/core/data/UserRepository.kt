package com.bookreads.core.data

import com.bookreads.core.common.CoreResult
import com.bookreads.core.network.api.BookLeaderboardApiService
import com.bookreads.core.network.api.UserModel

interface UserRepository {
    suspend fun registerOrGetUser(username: String): CoreResult<UserModel>

    suspend fun getUser(username: String): CoreResult<UserModel>
}

internal class UserRepositoryImpl(
    private val apiService: BookLeaderboardApiService,
) : UserRepository {
    override suspend fun registerOrGetUser(username: String): CoreResult<UserModel> =
        apiService.registerOrGetUser(username).toCoreResult()

    override suspend fun getUser(username: String): CoreResult<UserModel> = apiService.getUser(username).toCoreResult()
}
