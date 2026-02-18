package com.bookreads

import com.bookreads.dto.UserDto
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus

class UserControllerTest : BaseIntegrationTest() {

    @Autowired
    lateinit var restTemplate: TestRestTemplate

    data class CreateUserRequest(val username: String)

    @Test
    fun `create user returns 200 with username`() {
        val response = restTemplate.postForEntity(
            "/api/v1/users",
            CreateUserRequest("alice"),
            UserDto::class.java
        )
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body?.username).isEqualTo("alice")
    }

    @Test
    fun `create user is idempotent`() {
        val first = restTemplate.postForEntity("/api/v1/users", CreateUserRequest("bob"), UserDto::class.java)
        val second = restTemplate.postForEntity("/api/v1/users", CreateUserRequest("bob"), UserDto::class.java)
        assertThat(first.body?.id).isEqualTo(second.body?.id)
    }

    @Test
    fun `get user by username returns 200`() {
        restTemplate.postForEntity("/api/v1/users", CreateUserRequest("charlie"), UserDto::class.java)
        val response = restTemplate.getForEntity("/api/v1/users/charlie", UserDto::class.java)
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body?.username).isEqualTo("charlie")
    }

    @Test
    fun `get unknown user returns 404`() {
        val response = restTemplate.getForEntity("/api/v1/users/nobody", Map::class.java)
        assertThat(response.statusCode).isEqualTo(HttpStatus.NOT_FOUND)
    }

    @Test
    fun `create user with blank name returns 400`() {
        val response = restTemplate.postForEntity("/api/v1/users", CreateUserRequest("  "), Map::class.java)
        assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
    }
}
