package com.bookreads.core.network.ktor.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class UserRequestDto(
    @SerialName("username") val username: String,
)
