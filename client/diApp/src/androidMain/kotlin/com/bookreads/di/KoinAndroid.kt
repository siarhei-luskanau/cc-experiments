package com.bookreads.di

import android.content.Context
import com.bookreads.core.common.DispatcherSet
import com.bookreads.core.pref.PrefPathProvider
import kotlinx.coroutines.runBlocking
import okio.Path
import okio.Path.Companion.toPath
import org.koin.core.module.Module
import org.koin.dsl.module

actual val appPlatformModule: Module =
    module {
        single<PrefPathProvider> {
            val context: Context = get()
            val dispatcherSet: DispatcherSet = get()
            object : PrefPathProvider {
                override fun get(): Path =
                    runBlocking(dispatcherSet.ioDispatcher()) {
                        val file = context.filesDir.resolve("app.pref.json")
                        file.absolutePath.toPath()
                    }
            }
        }
    }
