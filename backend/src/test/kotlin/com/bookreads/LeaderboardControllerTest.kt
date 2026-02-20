package com.bookreads

import com.bookreads.dto.LeaderboardEntryDto
import com.bookreads.dto.SessionSyncRequest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.MediaType
import java.time.Instant
import java.util.UUID

class LeaderboardControllerTest : BaseIntegrationTest() {
    data class CreateUserRequest(
        val username: String,
    )

    private fun createUser(username: String) {
        client()
            .post()
            .uri("/api/v1/users")
            .contentType(MediaType.APPLICATION_JSON)
            .body(CreateUserRequest(username))
            .exchange { _, _ -> }
    }

    private fun uniqueUser() = "lb_${UUID.randomUUID().toString().take(8)}"

    @Test
    fun `leaderboard default window returns 200`() {
        client().get().uri("/api/v1/leaderboard").exchange { _, response ->
            assertThat(response.statusCode.value()).isEqualTo(200)
        }
    }

    @Test
    fun `leaderboard daily window returns 200`() {
        client().get().uri("/api/v1/leaderboard?window=daily").exchange { _, response ->
            assertThat(response.statusCode.value()).isEqualTo(200)
        }
    }

    @Test
    fun `leaderboard weekly window returns 200`() {
        client().get().uri("/api/v1/leaderboard?window=weekly").exchange { _, response ->
            assertThat(response.statusCode.value()).isEqualTo(200)
        }
    }

    @Test
    fun `leaderboard monthly window returns 200`() {
        client().get().uri("/api/v1/leaderboard?window=monthly").exchange { _, response ->
            assertThat(response.statusCode.value()).isEqualTo(200)
        }
    }

    @Test
    fun `leaderboard invalid window returns 400`() {
        client().get().uri("/api/v1/leaderboard?window=invalid").exchange { _, response ->
            assertThat(response.statusCode.value()).isEqualTo(400)
        }
    }

    @Test
    fun `user with synced session appears on leaderboard with correct duration`() {
        val username = uniqueUser()
        createUser(username)
        val syncRequest =
            SessionSyncRequest(
                clientId = UUID.randomUUID().toString(),
                username = username,
                bookTitle = "Dune",
                startedAt = Instant.now().toString(),
                endedAt = Instant.now().toString(),
                durationSec = 300,
            )
        client()
            .post()
            .uri("/api/v1/sessions/sync")
            .contentType(MediaType.APPLICATION_JSON)
            .body(syncRequest)
            .exchange { _, _ -> }

        client().get().uri("/api/v1/leaderboard?window=alltime").exchange { _, response ->
            assertThat(response.statusCode.value()).isEqualTo(200)
            val entries = response.bodyTo(object : ParameterizedTypeReference<List<LeaderboardEntryDto>>() {})
            val entry = entries?.find { it.username == username }
            assertThat(entry).isNotNull
            assertThat(entry?.totalSec).isEqualTo(300)
        }
    }
}
