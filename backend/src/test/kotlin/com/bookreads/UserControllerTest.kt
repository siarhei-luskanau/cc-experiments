package com.bookreads

import com.bookreads.dto.UserDto
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType

class UserControllerTest : BaseIntegrationTest() {
    data class CreateUserRequest(
        val username: String,
    )

    @Test
    fun `create user returns 200 with username`() {
        client()
            .post()
            .uri("/api/v1/users")
            .contentType(MediaType.APPLICATION_JSON)
            .body(CreateUserRequest("alice"))
            .exchange { _, response ->
                assertThat(response.statusCode.value()).isEqualTo(200)
                val user = response.bodyTo(UserDto::class.java)
                assertThat(user?.username).isEqualTo("alice")
            }
    }

    @Test
    fun `create user is idempotent`() {
        val first =
            client()
                .post()
                .uri("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .body(CreateUserRequest("bob"))
                .exchange { _, r -> r.bodyTo(UserDto::class.java) }
        val second =
            client()
                .post()
                .uri("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .body(CreateUserRequest("bob"))
                .exchange { _, r -> r.bodyTo(UserDto::class.java) }
        assertThat(first?.id).isEqualTo(second?.id)
    }

    @Test
    fun `get user by username returns 200`() {
        client()
            .post()
            .uri("/api/v1/users")
            .contentType(MediaType.APPLICATION_JSON)
            .body(CreateUserRequest("charlie"))
            .exchange { _, _ -> }
        client().get().uri("/api/v1/users/charlie").exchange { _, response ->
            assertThat(response.statusCode.value()).isEqualTo(200)
            val user = response.bodyTo(UserDto::class.java)
            assertThat(user?.username).isEqualTo("charlie")
        }
    }

    @Test
    fun `get unknown user returns 404`() {
        client().get().uri("/api/v1/users/nobody").exchange { _, response ->
            assertThat(response.statusCode.value()).isEqualTo(404)
        }
    }

    @Test
    fun `create user with blank name returns 400`() {
        client()
            .post()
            .uri("/api/v1/users")
            .contentType(MediaType.APPLICATION_JSON)
            .body(CreateUserRequest("  "))
            .exchange { _, response ->
                assertThat(response.statusCode.value()).isEqualTo(400)
            }
    }
}
