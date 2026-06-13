package com.bookreads.di

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.datastore.core.okio.OkioStorage
import com.bookreads.core.pref.StorageProvider
import com.github.takahirom.roborazzi.ExperimentalRoborazziApi
import io.github.takahirom.roborazzi.captureRoboImage
import kotlinx.cinterop.ExperimentalForeignApi
import okio.FileSystem
import okio.Path.Companion.toPath
import org.koin.dsl.module
import platform.Foundation.NSTemporaryDirectory
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class, ExperimentalRoborazziApi::class, ExperimentalForeignApi::class)
class KoinAppIosTest {
    private val testPlatformModule =
        module {
            single<StorageProvider> {
                val path = NSTemporaryDirectory() + "test.ios.screenshot.app.pref.json"
                object : StorageProvider {
                    override fun <T> getStorage(
                        serializer: androidx.datastore.core.okio.OkioSerializer<T>,
                    ): androidx.datastore.core.Storage<T> =
                        OkioStorage(
                            fileSystem = FileSystem.SYSTEM,
                            serializer = serializer,
                            producePath = { path.toPath() },
                        )
                }
            }
        }

    @Test
    fun preview() =
        runComposeUiTest {
            setContent { KoinApp(platformModule = testPlatformModule) }
            waitForIdle()
            awaitIdle()
            onRoot().captureRoboImage(this, filePath = "com.bookreads.di.KoinAppIosTest.preview.png")
        }
}
