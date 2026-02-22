package com.bookreads.core.network.api

data class SessionModel(
    val clientId: String,
    val username: String,
    val bookTitle: String,
    val startedAt: String,
    val endedAt: String?,
    val durationSec: Long,
)
