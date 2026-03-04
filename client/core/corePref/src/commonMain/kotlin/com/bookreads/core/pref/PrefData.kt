package com.bookreads.core.pref

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class PrefData(
    @SerialName("key") val key: String?,
    @SerialName("session") val session: String? = null,
)
