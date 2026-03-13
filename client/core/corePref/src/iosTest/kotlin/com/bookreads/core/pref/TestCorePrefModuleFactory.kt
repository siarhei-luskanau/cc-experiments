package com.bookreads.core.pref

import androidx.datastore.core.okio.OkioStorage
import kotlinx.cinterop.ExperimentalForeignApi
import okio.FileSystem
import okio.Path.Companion.toPath
import org.koin.dsl.module
import platform.Foundation.NSFileManager
import platform.Foundation.NSTemporaryDirectory

@OptIn(ExperimentalForeignApi::class)
private val TEST_PREF_PATH = NSTemporaryDirectory() + "test.app.pref.json"

@OptIn(ExperimentalForeignApi::class)
actual fun testPrefModule() =
    module {
        includes(corePrefModule)
        single<StorageProvider> {
            object : StorageProvider {
                override fun <T> getStorage(
                    serializer: androidx.datastore.core.okio.OkioSerializer<T>,
                ): androidx.datastore.core.Storage<T> =
                    OkioStorage(
                        fileSystem = FileSystem.SYSTEM,
                        serializer = serializer,
                        producePath = { TEST_PREF_PATH.toPath() },
                    )
            }
        }
    }

@OptIn(ExperimentalForeignApi::class)
actual fun cleanUpTestStorage() {
    NSFileManager.defaultManager.removeItemAtPath(TEST_PREF_PATH, error = null)
}
