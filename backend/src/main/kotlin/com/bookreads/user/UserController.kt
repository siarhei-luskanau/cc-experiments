package com.bookreads.user

import com.bookreads.dto.UserDto
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/users")
class UserController(
    private val userService: UserService,
) {
    data class CreateUserRequest(
        val username: String,
    )

    @PostMapping
    fun createOrGet(
        @RequestBody request: CreateUserRequest,
    ): ResponseEntity<UserDto> = ResponseEntity.ok(userService.getOrCreate(request.username))

    @GetMapping("/{username}")
    fun getUser(
        @PathVariable username: String,
    ): ResponseEntity<UserDto> = ResponseEntity.ok(userService.getByUsername(username))

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleBadRequest(ex: IllegalArgumentException): ResponseEntity<Map<String, String>> =
        ResponseEntity.badRequest().body(mapOf("error" to (ex.message ?: "Bad request")))

    @ExceptionHandler(NoSuchElementException::class)
    fun handleNotFound(ex: NoSuchElementException): ResponseEntity<Map<String, String>> =
        ResponseEntity.notFound().build()
}
