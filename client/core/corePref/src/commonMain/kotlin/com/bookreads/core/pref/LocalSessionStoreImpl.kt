package com.bookreads.core.pref

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json

internal class LocalSessionStoreImpl(
    private val prefService: PrefService,
) : LocalSessionStore {
    private val json = Json { ignoreUnknownKeys = true }

    override fun observe(): Flow<ActiveSession?> =
        prefService.getSessionJson().map { raw ->
            raw?.let {
                try {
                    json.decodeFromString(ActiveSession.serializer(), it)
                } catch (_: Throwable) {
                    null
                }
            }
        }

    override suspend fun save(session: ActiveSession) {
        prefService.setSessionJson(json.encodeToString(ActiveSession.serializer(), session))
    }

    override suspend fun clear() {
        prefService.setSessionJson(null)
    }
}
