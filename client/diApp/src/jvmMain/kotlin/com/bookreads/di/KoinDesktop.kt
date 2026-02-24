package com.bookreads.di

import com.bookreads.core.pref.PrefPathProvider
import okio.Path
import okio.Path.Companion.toPath
import org.koin.core.module.Module
import org.koin.dsl.module
import java.io.File

actual val appPlatformModule: Module =
    module {
        single<PrefPathProvider> {
            object : PrefPathProvider {
                override fun get(): Path {
                    val dir = File(System.getProperty("user.home"), ".bookreads")
                    dir.mkdirs()
                    return File(dir, "app.pref.json").absolutePath.toPath()
                }
            }
        }
    }
