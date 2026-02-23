package com.bookreads.dto

import kotlinx.serialization.Serializable

@Serializable
data class SessionDto(
    val clientId: String,
    val username: String,
    val bookTitle: String,
    val startedAt: String,
    val endedAt: String?,
    val durationSec: Long,
)
