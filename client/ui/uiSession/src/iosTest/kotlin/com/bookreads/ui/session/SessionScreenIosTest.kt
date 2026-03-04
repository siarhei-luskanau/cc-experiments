package com.bookreads.ui.session

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.runComposeUiTest
import com.github.takahirom.roborazzi.ExperimentalRoborazziApi
import io.github.takahirom.roborazzi.captureRoboImage
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class, ExperimentalRoborazziApi::class)
class SessionScreenIosTest {
    @Test
    fun previewIdle() =
        runComposeUiTest {
            setContent {
                SessionScreenIdlePreview()
            }
            onRoot().captureRoboImage(this, filePath = "com.bookreads.ui.session.SessionScreenIosTest.previewIdle.png")
        }

    @Test
    fun previewReading() =
        runComposeUiTest {
            setContent {
                SessionScreenReadingPreview()
            }
            onRoot().captureRoboImage(
                this,
                filePath = "com.bookreads.ui.session.SessionScreenIosTest.previewReading.png",
            )
        }
}
