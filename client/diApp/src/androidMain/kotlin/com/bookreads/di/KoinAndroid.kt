package com.bookreads.di

import android.content.Context
import androidx.datastore.core.okio.OkioStorage
import com.bookreads.core.common.DispatcherSet
import com.bookreads.core.pref.StorageProvider
import kotlinx.coroutines.runBlocking
import okio.FileSystem
import okio.Path.Companion.toPath
import org.koin.core.module.Module
import org.koin.dsl.module

actual val appPlatformModule: Module =
    module {
        single<StorageProvider> {
            val context: Context = get()
            val dispatcherSet: DispatcherSet = get()
            object : StorageProvider {
                override fun <T> getStorage(
                    serializer: androidx.datastore.core.okio.OkioSerializer<T>,
                ): androidx.datastore.core.Storage<T> =
                    OkioStorage(
                        fileSystem = FileSystem.SYSTEM,
                        serializer = serializer,
                        producePath = {
                            runBlocking(dispatcherSet.ioDispatcher()) {
                                context.filesDir
                                    .resolve("app.pref.json")
                                    .absolutePath
                                    .toPath()
                            }
                        },
                    )
            }
        }
    }
