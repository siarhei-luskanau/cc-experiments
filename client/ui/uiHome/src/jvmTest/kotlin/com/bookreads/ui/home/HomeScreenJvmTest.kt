package com.bookreads.ui.home

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.v2.runComposeUiTest
import io.github.takahirom.roborazzi.captureRoboImage
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class HomeScreenJvmTest {
    @Test
    fun preview() =
        runComposeUiTest {
            setContent { HomeScreenPreview() }
            waitForIdle()
            awaitIdle()
            onRoot().captureRoboImage()
        }
}
