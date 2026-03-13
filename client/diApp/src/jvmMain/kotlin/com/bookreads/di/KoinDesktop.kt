package com.bookreads.di

import androidx.datastore.core.okio.OkioStorage
import com.bookreads.core.pref.StorageProvider
import okio.FileSystem
import okio.Path.Companion.toPath
import org.koin.core.module.Module
import org.koin.dsl.module
import java.io.File

actual val appPlatformModule: Module =
    module {
        single<StorageProvider> {
            object : StorageProvider {
                override fun <T> getStorage(
                    serializer: androidx.datastore.core.okio.OkioSerializer<T>,
                ): androidx.datastore.core.Storage<T> {
                    val dir = File(System.getProperty("user.home"), ".bookreads")
                    dir.mkdirs()
                    return OkioStorage(
                        fileSystem = FileSystem.SYSTEM,
                        serializer = serializer,
                        producePath = { File(dir, "app.pref.json").absolutePath.toPath() },
                    )
                }
            }
        }
    }
