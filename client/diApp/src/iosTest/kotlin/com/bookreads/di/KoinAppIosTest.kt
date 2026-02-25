package com.bookreads.di

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.runComposeUiTest
import com.bookreads.core.pref.PrefPathProvider
import com.github.takahirom.roborazzi.ExperimentalRoborazziApi
import io.github.takahirom.roborazzi.captureRoboImage
import kotlinx.cinterop.ExperimentalForeignApi
import okio.Path.Companion.toPath
import org.koin.dsl.module
import platform.Foundation.NSTemporaryDirectory
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class, ExperimentalRoborazziApi::class, ExperimentalForeignApi::class)
class KoinAppIosTest {
    private val testPlatformModule =
        module {
            single<PrefPathProvider> {
                val path = NSTemporaryDirectory() + "test.ios.screenshot.app.pref.json"
                object : PrefPathProvider {
                    override fun get() = path.toPath()
                }
            }
        }

    @Test
    fun preview() =
        runComposeUiTest {
            setContent {
                KoinApp(platformModule = testPlatformModule)
            }
            onRoot().captureRoboImage(this, filePath = "com.bookreads.di.KoinAppIosTest.preview.png")
        }
}
