package com.bookreads

import com.bookreads.dto.LeaderboardEntryDto
import com.bookreads.dto.SessionDto
import com.bookreads.dto.UserDto
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus
import java.util.UUID

class LeaderboardControllerTest : BaseIntegrationTest() {

    @Autowired
    lateinit var restTemplate: TestRestTemplate

    data class CreateUserRequest(val username: String)
    data class StartSessionRequest(val username: String, val bookTitle: String)
    data class EndSessionRequest(val username: String)

    private fun createUser(username: String): UserDto =
        restTemplate.postForEntity("/api/v1/users", CreateUserRequest(username), UserDto::class.java).body!!

    private fun uniqueUser(): String = "lb_${UUID.randomUUID().toString().take(8)}"

    @Test
    fun `leaderboard default window returns 200`() {
        val response = restTemplate.getForEntity("/api/v1/leaderboard", List::class.java)
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
    }

    @Test
    fun `leaderboard daily window returns 200`() {
        val response = restTemplate.getForEntity("/api/v1/leaderboard?window=daily", List::class.java)
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
    }

    @Test
    fun `leaderboard weekly window returns 200`() {
        val response = restTemplate.getForEntity("/api/v1/leaderboard?window=weekly", List::class.java)
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
    }

    @Test
    fun `leaderboard monthly window returns 200`() {
        val response = restTemplate.getForEntity("/api/v1/leaderboard?window=monthly", List::class.java)
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
    }

    @Test
    fun `leaderboard invalid window returns 400`() {
        val response = restTemplate.getForEntity("/api/v1/leaderboard?window=invalid", Map::class.java)
        assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
    }

    @Test
    fun `user with completed session appears on leaderboard`() {
        val username = uniqueUser()
        createUser(username)
        restTemplate.postForEntity("/api/v1/sessions/start", StartSessionRequest(username, "Dune"), SessionDto::class.java)
        Thread.sleep(1100)
        restTemplate.postForEntity("/api/v1/sessions/end", EndSessionRequest(username), SessionDto::class.java)

        val response = restTemplate.getForEntity("/api/v1/leaderboard?window=alltime", Array<LeaderboardEntryDto>::class.java)
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        val entry = response.body?.find { it.username == username }
        assertThat(entry).isNotNull
        assertThat(entry?.totalSec).isGreaterThan(0)
    }
}
