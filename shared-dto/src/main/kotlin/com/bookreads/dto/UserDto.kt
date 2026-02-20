package com.bookreads.dto

import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    val id: Long,
    val username: String,
    val createdAt: String,
)
