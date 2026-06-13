package com.bookreads.ui.session

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.v2.runComposeUiTest
import io.github.takahirom.roborazzi.captureRoboImage
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class SessionScreenJvmTest {
    @Test
    fun previewIdle() =
        runComposeUiTest {
            setContent { SessionScreenIdlePreview() }
            waitForIdle()
            awaitIdle()
            onRoot().captureRoboImage()
        }

    @Test
    fun previewReading() =
        runComposeUiTest {
            setContent { SessionScreenReadingPreview() }
            waitForIdle()
            awaitIdle()
            onRoot().captureRoboImage()
        }
}
