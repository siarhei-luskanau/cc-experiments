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
                    val storageFile =
                        File(
                            listOf(
                                System.getProperty("user.home"),
                                ".bookreads",
                                "datastore",
                                "app.pref.json",
                            ).joinToString(separator = File.separator),
                        ).also { it.parentFile?.mkdirs() }
                    return OkioStorage(
                        fileSystem = FileSystem.SYSTEM,
                        serializer = serializer,
                        producePath = { storageFile.absolutePath.toPath() },
                    )
                }
            }
        }
    }
