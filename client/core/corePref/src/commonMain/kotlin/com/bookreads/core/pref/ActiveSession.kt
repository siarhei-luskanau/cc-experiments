package com.bookreads.core.pref

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ActiveSession(
    @SerialName("clientId") val clientId: String,
    @SerialName("username") val username: String,
    @SerialName("bookTitle") val bookTitle: String,
    @SerialName("startedAt") val startedAt: String,
    @SerialName("elapsedOffsetSec") val elapsedOffsetSec: Long,
)
