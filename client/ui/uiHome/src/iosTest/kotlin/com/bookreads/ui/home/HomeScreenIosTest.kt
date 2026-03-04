package com.bookreads.ui.home

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.runComposeUiTest
import com.github.takahirom.roborazzi.ExperimentalRoborazziApi
import io.github.takahirom.roborazzi.captureRoboImage
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class, ExperimentalRoborazziApi::class)
class HomeScreenIosTest {
    @Test
    fun preview() =
        runComposeUiTest {
            setContent {
                HomeScreenPreview()
            }
            onRoot().captureRoboImage(this, filePath = "com.bookreads.ui.home.HomeScreenIosTest.preview.png")
        }
}
