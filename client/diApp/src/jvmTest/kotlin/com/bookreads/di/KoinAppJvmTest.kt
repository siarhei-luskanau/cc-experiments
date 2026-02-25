package com.bookreads.di

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.runDesktopComposeUiTest
import com.bookreads.core.pref.PrefPathProvider
import io.github.takahirom.roborazzi.captureRoboImage
import okio.Path.Companion.toPath
import org.koin.dsl.module
import java.nio.file.Files
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class KoinAppJvmTest {
    private val testPlatformModule =
        module {
            single<PrefPathProvider> {
                val tempFile = Files.createTempFile("test.jvm.screenshot", ".app.pref.json").toFile()
                tempFile.deleteOnExit()
                object : PrefPathProvider {
                    override fun get() = tempFile.absolutePath.toPath()
                }
            }
        }

    @Test
    fun preview() =
        runDesktopComposeUiTest {
            setContent {
                KoinApp(platformModule = testPlatformModule)
            }
            onRoot().captureRoboImage()
        }
}
