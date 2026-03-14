package com.bookreads.di

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.runDesktopComposeUiTest
import androidx.datastore.core.okio.OkioStorage
import com.bookreads.core.pref.StorageProvider
import io.github.takahirom.roborazzi.captureRoboImage
import okio.FileSystem
import okio.Path.Companion.toPath
import org.koin.dsl.module
import java.nio.file.Files
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class KoinAppJvmTest {
    private val testPlatformModule =
        module {
            single<StorageProvider> {
                val tempFile = Files.createTempFile("test.jvm.screenshot", ".app.pref.json").toFile()
                tempFile.deleteOnExit()
                object : StorageProvider {
                    override fun <T> getStorage(
                        serializer: androidx.datastore.core.okio.OkioSerializer<T>,
                    ): androidx.datastore.core.Storage<T> =
                        OkioStorage(
                            fileSystem = FileSystem.SYSTEM,
                            serializer = serializer,
                            producePath = { tempFile.absolutePath.toPath() },
                        )
                }
            }
        }

    @Test
    fun preview() =
        runDesktopComposeUiTest {
            setContent {
                KoinApp(platformModule = testPlatformModule)
            }
            waitForIdle()
            waitForIdle()
            onRoot().captureRoboImage()
        }
}
