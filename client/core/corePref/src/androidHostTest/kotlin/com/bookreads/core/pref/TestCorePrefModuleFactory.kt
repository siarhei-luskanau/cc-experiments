package com.bookreads.core.pref

import androidx.datastore.core.okio.OkioStorage
import okio.FileSystem
import okio.Path.Companion.toPath
import org.koin.dsl.module
import java.io.File

private val TEST_PREF_FILE = File(System.getProperty("java.io.tmpdir"), "test.app.pref.json")

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
                        producePath = { TEST_PREF_FILE.absolutePath.toPath() },
                    )
            }
        }
    }

actual fun cleanUpTestStorage() {
    TEST_PREF_FILE.delete()
}
