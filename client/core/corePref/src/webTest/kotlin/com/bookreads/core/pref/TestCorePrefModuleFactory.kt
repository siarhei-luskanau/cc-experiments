package com.bookreads.core.pref

import androidx.datastore.core.okio.WebStorage
import androidx.datastore.core.okio.WebStorageType
import kotlinx.browser.localStorage
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun testPrefModule(): Module =
    module {
        includes(corePrefModule)
        single<StorageProvider> {
            object : StorageProvider {
                override fun <T> getStorage(
                    serializer: androidx.datastore.core.okio.OkioSerializer<T>,
                ): androidx.datastore.core.Storage<T> = WebStorage(serializer, "test.app.pref", WebStorageType.LOCAL)
            }
        }
    }

actual fun cleanUpTestStorage() {
    localStorage.clear()
}
