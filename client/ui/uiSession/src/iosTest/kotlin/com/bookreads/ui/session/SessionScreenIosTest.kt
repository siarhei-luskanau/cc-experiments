package com.bookreads.ui.session

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.v2.runComposeUiTest
import com.github.takahirom.roborazzi.ExperimentalRoborazziApi
import io.github.takahirom.roborazzi.captureRoboImage
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class, ExperimentalRoborazziApi::class)
class SessionScreenIosTest {
    @Test
    fun previewIdle() =
        runComposeUiTest {
            setContent { SessionScreenIdlePreview() }
            waitForIdle()
            awaitIdle()
            onRoot().captureRoboImage(this, filePath = "com.bookreads.ui.session.SessionScreenIosTest.previewIdle.png")
        }

    @Test
    fun previewReading() =
        runComposeUiTest {
            setContent { SessionScreenReadingPreview() }
            waitForIdle()
            awaitIdle()
            onRoot().captureRoboImage(
                this,
                filePath = "com.bookreads.ui.session.SessionScreenIosTest.previewReading.png",
            )
        }
}
