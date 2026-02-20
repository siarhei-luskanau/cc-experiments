package com.bookreads.user

import com.bookreads.dto.UserDto
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService(
    private val userRepository: UserRepository,
) {
    @Transactional
    fun getOrCreate(username: String): UserDto {
        val trimmed = username.trim()
        require(trimmed.isNotBlank()) { "Username must not be blank" }
        val user =
            userRepository
                .findByUsername(trimmed)
                .orElseGet { userRepository.save(User(username = trimmed)) }
        return user.toDto()
    }

    @Transactional(readOnly = true)
    fun getByUsername(username: String): UserDto {
        val user =
            userRepository
                .findByUsername(username.trim())
                .orElseThrow { NoSuchElementException("User not found: $username") }
        return user.toDto()
    }

    private fun User.toDto() = UserDto(id = id, username = username, createdAt = createdAt.toString())
}
