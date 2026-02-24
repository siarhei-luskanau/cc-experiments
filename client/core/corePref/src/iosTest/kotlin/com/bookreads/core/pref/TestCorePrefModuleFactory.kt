package com.bookreads.core.pref

import kotlinx.cinterop.ExperimentalForeignApi
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
        single<PrefPathProvider> {
            object : PrefPathProvider {
                override fun get() = TEST_PREF_PATH.toPath()
            }
        }
    }

@OptIn(ExperimentalForeignApi::class)
actual fun cleanUpTestStorage() {
    NSFileManager.defaultManager.removeItemAtPath(TEST_PREF_PATH, error = null)
}
