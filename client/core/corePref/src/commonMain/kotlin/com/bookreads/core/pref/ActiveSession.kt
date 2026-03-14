package com.bookreads.core.pref

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PendingStop(
    @SerialName("endedAt") val endedAt: String,
    @SerialName("durationSec") val durationSec: Long,
)

@Serializable
data class ActiveSession(
    @SerialName("clientId") val clientId: String,
    @SerialName("username") val username: String,
    @SerialName("bookTitle") val bookTitle: String,
    @SerialName("startedAt") val startedAt: String,
    @SerialName("elapsedOffsetSec") val elapsedOffsetSec: Long,
    @SerialName("pendingStop") val pendingStop: PendingStop? = null,
)
