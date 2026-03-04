package com.bookreads.ui.session

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.runDesktopComposeUiTest
import io.github.takahirom.roborazzi.captureRoboImage
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class SessionScreenJvmTest {
    @Test
    fun previewIdle() =
        runDesktopComposeUiTest {
            setContent {
                SessionScreenIdlePreview()
            }
            onRoot().captureRoboImage()
        }

    @Test
    fun previewReading() =
        runDesktopComposeUiTest {
            setContent {
                SessionScreenReadingPreview()
            }
            onRoot().captureRoboImage()
        }
}
