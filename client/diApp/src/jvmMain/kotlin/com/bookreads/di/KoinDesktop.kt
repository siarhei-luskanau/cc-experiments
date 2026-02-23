package com.bookreads.di

import com.bookreads.core.common.DispatcherSet
import com.bookreads.core.pref.PrefPathProvider
import kotlinx.coroutines.runBlocking
import okio.Path
import okio.Path.Companion.toPath
import org.koin.core.module.Module
import org.koin.dsl.module
import java.io.File

actual val appPlatformModule: Module =
    module {
        single<PrefPathProvider> {
            val dispatcherSet: DispatcherSet = get()
            object : PrefPathProvider {
                override fun get(): Path =
                    runBlocking(dispatcherSet.ioDispatcher()) {
                        val file = File.createTempFile("temp_", "app.pref.json")
                        file.absolutePath.toPath()
                    }
            }
        }
    }
