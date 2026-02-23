package com.bookreads.core.network.ktor

import com.bookreads.core.network.ktor.model.UserRequestDto
import com.bookreads.dto.LeaderboardEntryDto
import com.bookreads.dto.SessionDto
import com.bookreads.dto.SessionSyncRequest
import com.bookreads.dto.UserDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

internal class BookLeaderboardApiClient(
    private val baseUrl: String = DEFAULT_BASE_URL,
) {
    private val httpClient: HttpClient by lazy {
        HttpClient {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }
    }

    internal suspend fun registerOrGetUser(username: String): UserDto =
        httpClient
            .post("$baseUrl/users") {
                contentType(ContentType.Application.Json)
                setBody(UserRequestDto(username = username))
            }.body()

    internal suspend fun getUser(username: String): UserDto = httpClient.get("$baseUrl/users/$username").body()

    internal suspend fun syncSession(request: SessionSyncRequest): SessionDto =
        httpClient
            .post("$baseUrl/sessions/sync") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }.body()

    internal suspend fun getSession(clientId: String): SessionDto? =
        httpClient.get("$baseUrl/sessions/$clientId").body()

    internal suspend fun getLeaderboard(window: String): List<LeaderboardEntryDto> =
        httpClient
            .get("$baseUrl/leaderboard") {
                url { parameters.append("window", window) }
            }.body()

    companion object {
        internal const val DEFAULT_BASE_URL = "http://localhost:8080/api/v1"
    }
}
