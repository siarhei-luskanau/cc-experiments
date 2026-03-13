package com.bookreads.di

import androidx.datastore.core.okio.WebStorage
import androidx.datastore.core.okio.WebStorageType
import com.bookreads.core.pref.StorageProvider
import org.koin.core.module.Module
import org.koin.dsl.module

actual val appPlatformModule: Module =
    module {
        single<StorageProvider> {
            object : StorageProvider {
                override fun <T> getStorage(
                    serializer: androidx.datastore.core.okio.OkioSerializer<T>,
                ): androidx.datastore.core.Storage<T> = WebStorage(serializer, "app.pref", WebStorageType.LOCAL)
            }
        }
    }
