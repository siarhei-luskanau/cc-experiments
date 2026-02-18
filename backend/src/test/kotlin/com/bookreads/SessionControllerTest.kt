package com.bookreads

import com.bookreads.dto.SessionDto
import com.bookreads.dto.UserDto
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus
import java.util.UUID

class SessionControllerTest : BaseIntegrationTest() {
    @Autowired
    lateinit var restTemplate: TestRestTemplate

    data class CreateUserRequest(
        val username: String,
    )

    data class StartSessionRequest(
        val username: String,
        val bookTitle: String,
    )

    data class EndSessionRequest(
        val username: String,
    )

    private fun createUser(username: String): UserDto =
        restTemplate.postForEntity("/api/v1/users", CreateUserRequest(username), UserDto::class.java).body!!

    private fun uniqueUser(): String = "user_${UUID.randomUUID().toString().take(8)}"

    @Test
    fun `start session returns 200 with null endedAt`() {
        val username = uniqueUser()
        createUser(username)
        val response =
            restTemplate.postForEntity(
                "/api/v1/sessions/start",
                StartSessionRequest(username, "Dune"),
                SessionDto::class.java,
            )
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body?.endedAt).isNull()
        assertThat(response.body?.bookTitle).isEqualTo("Dune")
    }

    @Test
    fun `start session when already active returns 409`() {
        val username = uniqueUser()
        createUser(username)
        restTemplate.postForEntity(
            "/api/v1/sessions/start",
            StartSessionRequest(username, "Book1"),
            SessionDto::class.java,
        )
        val response =
            restTemplate.postForEntity(
                "/api/v1/sessions/start",
                StartSessionRequest(username, "Book2"),
                Map::class.java,
            )
        assertThat(response.statusCode).isEqualTo(HttpStatus.CONFLICT)
    }

    @Test
    fun `end session returns 200 with endedAt set`() {
        val username = uniqueUser()
        createUser(username)
        restTemplate.postForEntity(
            "/api/v1/sessions/start",
            StartSessionRequest(username, "Dune"),
            SessionDto::class.java,
        )
        val response =
            restTemplate.postForEntity(
                "/api/v1/sessions/end",
                EndSessionRequest(username),
                SessionDto::class.java,
            )
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body?.endedAt).isNotNull()
    }

    @Test
    fun `end session with no active session returns 404`() {
        val username = uniqueUser()
        createUser(username)
        val response = restTemplate.postForEntity("/api/v1/sessions/end", EndSessionRequest(username), Map::class.java)
        assertThat(response.statusCode).isEqualTo(HttpStatus.NOT_FOUND)
    }

    @Test
    fun `get active session returns 200 when active`() {
        val username = uniqueUser()
        createUser(username)
        restTemplate.postForEntity(
            "/api/v1/sessions/start",
            StartSessionRequest(username, "Dune"),
            SessionDto::class.java,
        )
        val response = restTemplate.getForEntity("/api/v1/sessions/active/$username", SessionDto::class.java)
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
    }

    @Test
    fun `get active session returns 204 when none`() {
        val username = uniqueUser()
        createUser(username)
        val response = restTemplate.getForEntity("/api/v1/sessions/active/$username", SessionDto::class.java)
        assertThat(response.statusCode).isEqualTo(HttpStatus.NO_CONTENT)
    }
}
