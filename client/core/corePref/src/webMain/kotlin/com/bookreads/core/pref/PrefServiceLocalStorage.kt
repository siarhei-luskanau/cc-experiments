package com.bookreads.core.pref

import kotlinx.browser.localStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json

private const val PREF_STORAGE_KEY = "app.pref.json"

internal class PrefServiceLocalStorage : PrefService {
    private val parser by lazy { Json { prettyPrint = true } }
    private val storageJson by lazy { Json { ignoreUnknownKeys = true } }

    private val prefFlow: MutableStateFlow<PrefData> by lazy {
        MutableStateFlow(
            localStorage.getItem(PREF_STORAGE_KEY)?.let { json ->
                try {
                    storageJson.decodeFromString(PrefData.serializer(), json)
                } catch (_: Throwable) {
                    PrefData(key = null)
                }
            } ?: PrefData(key = null),
        )
    }

    override fun getUserPreferenceContent(): Flow<String?> = prefFlow.map { parser.encodeToString(it) }

    override fun getKey(): Flow<String?> = prefFlow.map { it.key }

    override suspend fun setKey(key: String?) {
        val newPrefData = prefFlow.first().copy(key = key)
        localStorage.setItem(PREF_STORAGE_KEY, storageJson.encodeToString(PrefData.serializer(), newPrefData))
        prefFlow.emit(newPrefData)
    }

    override fun getSessionJson(): Flow<String?> = prefFlow.map { it.session }

    override suspend fun setSessionJson(json: String?) {
        val newPrefData = prefFlow.first().copy(session = json)
        localStorage.setItem(PREF_STORAGE_KEY, storageJson.encodeToString(PrefData.serializer(), newPrefData))
        prefFlow.emit(newPrefData)
    }
}
