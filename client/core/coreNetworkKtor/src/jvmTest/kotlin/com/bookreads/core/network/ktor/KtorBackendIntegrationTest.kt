package com.bookreads.core.network.ktor

import com.bookreads.Application
import com.bookreads.core.network.api.LeaderboardEntryModel
import com.bookreads.core.network.api.NetworkResult
import com.bookreads.core.network.api.SessionModel
import com.bookreads.core.network.api.SessionSyncModel
import com.bookreads.core.network.api.UserModel
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.postgresql.PostgreSQLContainer
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@SpringBootTest(
    classes = [Application::class],
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
)
class KtorBackendIntegrationTest {
    @LocalServerPort
    private var port: Int = 0

    private val service
        get() =
            KtorBookLeaderboardApiService(
                BookLeaderboardApiClient(baseUrl = "http://localhost:$port/api/v1"),
            )

    companion object {
        private val postgres = PostgreSQLContainer("postgres:16-alpine").apply { start() }

        @JvmStatic
        @DynamicPropertySource
        fun properties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url", postgres::getJdbcUrl)
            registry.add("spring.datasource.username", postgres::getUsername)
            registry.add("spring.datasource.password", postgres::getPassword)
        }
    }

    @Test
    fun registerOrGetUser_createsAndReturnsUser() =
        runTest {
            val result = service.registerOrGetUser("alice")
            assertIs<NetworkResult.Success<UserModel>>(result)
            assertEquals("alice", result.data.username)
            assertTrue(result.data.id > 0)
        }

    @Test
    fun registerOrGetUser_isIdempotent() =
        runTest {
            val first = service.registerOrGetUser("bob")
            val second = service.registerOrGetUser("bob")
            assertIs<NetworkResult.Success<UserModel>>(first)
            assertIs<NetworkResult.Success<UserModel>>(second)
            assertEquals(first.data.id, second.data.id)
        }

    @Test
    fun getUser_returnsExistingUser() =
        runTest {
            service.registerOrGetUser("carol")
            val result = service.getUser("carol")
            assertIs<NetworkResult.Success<UserModel>>(result)
            assertEquals("carol", result.data.username)
        }

    @Test
    fun syncSession_upsertsAndReturnsSession() =
        runTest {
            service.registerOrGetUser("dave")
            val sync =
                SessionSyncModel(
                    clientId = "session-dave-1",
                    username = "dave",
                    bookTitle = "Kotlin in Action",
                    startedAt = "2024-01-01T10:00:00Z",
                    endedAt = null,
                    durationSec = 300L,
                )
            val result = service.syncSession(sync)
            assertIs<NetworkResult.Success<SessionModel>>(result)
            assertEquals("session-dave-1", result.data.clientId)
            assertEquals(300L, result.data.durationSec)
        }

    @Test
    fun syncSession_updatesExistingSession() =
        runTest {
            service.registerOrGetUser("eve")
            val clientId = "session-eve-1"
            service.syncSession(
                SessionSyncModel(
                    clientId = clientId,
                    username = "eve",
                    bookTitle = "Clean Code",
                    startedAt = "2024-01-01T09:00:00Z",
                    endedAt = null,
                    durationSec = 600L,
                ),
            )
            val updated =
                service.syncSession(
                    SessionSyncModel(
                        clientId = clientId,
                        username = "eve",
                        bookTitle = "Clean Code",
                        startedAt = "2024-01-01T09:00:00Z",
                        endedAt = "2024-01-01T10:00:00Z",
                        durationSec = 3600L,
                    ),
                )
            assertIs<NetworkResult.Success<SessionModel>>(updated)
            assertEquals(3600L, updated.data.durationSec)
            assertNotNull(updated.data.endedAt)
        }

    @Test
    fun getSession_returnsSessionAfterSync() =
        runTest {
            service.registerOrGetUser("frank")
            service.syncSession(
                SessionSyncModel(
                    clientId = "session-frank-1",
                    username = "frank",
                    bookTitle = "The Pragmatic Programmer",
                    startedAt = "2024-01-01T08:00:00Z",
                    endedAt = null,
                    durationSec = 1800L,
                ),
            )
            val result = service.getSession("session-frank-1")
            assertIs<NetworkResult.Success<SessionModel?>>(result)
            assertEquals("session-frank-1", result.data?.clientId)
            assertEquals(1800L, result.data?.durationSec)
        }

    @Test
    fun getLeaderboard_returnsRankedEntries() =
        runTest {
            service.registerOrGetUser("grace")
            service.syncSession(
                SessionSyncModel(
                    clientId = "session-grace-1",
                    username = "grace",
                    bookTitle = "Domain-Driven Design",
                    startedAt = "2024-01-01T07:00:00Z",
                    endedAt = "2024-01-01T09:00:00Z",
                    durationSec = 7200L,
                ),
            )
            val result = service.getLeaderboard("alltime")
            assertIs<NetworkResult.Success<List<LeaderboardEntryModel>>>(result)
            assertTrue(result.data.isNotEmpty())
            val ranks = result.data.map { it.rank }
            assertEquals(ranks.sorted(), ranks)
        }
}
