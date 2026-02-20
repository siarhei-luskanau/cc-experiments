package com.bookreads

import com.bookreads.dto.SessionDto
import com.bookreads.dto.SessionSyncRequest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import java.time.Instant
import java.util.UUID

class SessionControllerTest : BaseIntegrationTest() {
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

    private fun uniqueUser() = "user_${UUID.randomUUID().toString().take(8)}"

    @Test
    fun `sync creates new session and returns 200`() {
        val username = uniqueUser()
        createUser(username)
        val clientId = UUID.randomUUID().toString()
        val request =
            SessionSyncRequest(
                clientId = clientId,
                username = username,
                bookTitle = "Dune",
                startedAt = Instant.now().toString(),
                endedAt = null,
                durationSec = 0,
            )
        client()
            .post()
            .uri("/api/v1/sessions/sync")
            .contentType(MediaType.APPLICATION_JSON)
            .body(request)
            .exchange { _, response ->
                assertThat(response.statusCode.value()).isEqualTo(200)
                val session = response.bodyTo(SessionDto::class.java)
                assertThat(session?.clientId).isEqualTo(clientId)
                assertThat(session?.bookTitle).isEqualTo("Dune")
                assertThat(session?.endedAt).isNull()
            }
    }

    @Test
    fun `sync updates existing session duration`() {
        val username = uniqueUser()
        createUser(username)
        val clientId = UUID.randomUUID().toString()
        val startedAt = Instant.now().toString()
        val initial = SessionSyncRequest(clientId, username, "Foundation", startedAt, null, 30)
        client()
            .post()
            .uri("/api/v1/sessions/sync")
            .contentType(MediaType.APPLICATION_JSON)
            .body(initial)
            .exchange { _, _ -> }
        val update = initial.copy(durationSec = 90)
        client()
            .post()
            .uri("/api/v1/sessions/sync")
            .contentType(MediaType.APPLICATION_JSON)
            .body(update)
            .exchange { _, response ->
                assertThat(response.statusCode.value()).isEqualTo(200)
                val session = response.bodyTo(SessionDto::class.java)
                assertThat(session?.durationSec).isEqualTo(90)
            }
    }

    @Test
    fun `sync with endedAt marks session as ended`() {
        val username = uniqueUser()
        createUser(username)
        val clientId = UUID.randomUUID().toString()
        val startedAt = Instant.now().toString()
        val initial = SessionSyncRequest(clientId, username, "Dune", startedAt, null, 0)
        client()
            .post()
            .uri("/api/v1/sessions/sync")
            .contentType(MediaType.APPLICATION_JSON)
            .body(initial)
            .exchange { _, _ -> }
        val final = initial.copy(endedAt = Instant.now().toString(), durationSec = 120)
        client()
            .post()
            .uri("/api/v1/sessions/sync")
            .contentType(MediaType.APPLICATION_JSON)
            .body(final)
            .exchange { _, response ->
                assertThat(response.statusCode.value()).isEqualTo(200)
                val session = response.bodyTo(SessionDto::class.java)
                assertThat(session?.endedAt).isNotNull()
                assertThat(session?.durationSec).isEqualTo(120)
            }
    }

    @Test
    fun `get session by clientId returns 200`() {
        val username = uniqueUser()
        createUser(username)
        val clientId = UUID.randomUUID().toString()
        val request = SessionSyncRequest(clientId, username, "Neuromancer", Instant.now().toString(), null, 0)
        client()
            .post()
            .uri("/api/v1/sessions/sync")
            .contentType(MediaType.APPLICATION_JSON)
            .body(request)
            .exchange { _, _ -> }
        client().get().uri("/api/v1/sessions/$clientId").exchange { _, response ->
            assertThat(response.statusCode.value()).isEqualTo(200)
            val session = response.bodyTo(SessionDto::class.java)
            assertThat(session?.clientId).isEqualTo(clientId)
        }
    }

    @Test
    fun `get session with unknown clientId returns 404`() {
        client().get().uri("/api/v1/sessions/${UUID.randomUUID()}").exchange { _, response ->
            assertThat(response.statusCode.value()).isEqualTo(404)
        }
    }

    @Test
    fun `sync with unknown user returns 404`() {
        val request =
            SessionSyncRequest(
                clientId = UUID.randomUUID().toString(),
                username = "ghost_${UUID.randomUUID()}",
                bookTitle = "Book",
                startedAt = Instant.now().toString(),
                endedAt = null,
                durationSec = 0,
            )
        client()
            .post()
            .uri("/api/v1/sessions/sync")
            .contentType(MediaType.APPLICATION_JSON)
            .body(request)
            .exchange { _, response ->
                assertThat(response.statusCode.value()).isEqualTo(404)
            }
    }
}
