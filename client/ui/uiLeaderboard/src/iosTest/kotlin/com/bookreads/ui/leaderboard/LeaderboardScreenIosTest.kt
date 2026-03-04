package com.bookreads.ui.leaderboard

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.runComposeUiTest
import com.github.takahirom.roborazzi.ExperimentalRoborazziApi
import io.github.takahirom.roborazzi.captureRoboImage
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class, ExperimentalRoborazziApi::class)
class LeaderboardScreenIosTest {
    @Test
    fun preview() =
        runComposeUiTest {
            setContent {
                LeaderboardScreenPreview()
            }
            onRoot().captureRoboImage(
                this,
                filePath = "com.bookreads.ui.leaderboard.LeaderboardScreenIosTest.preview.png",
            )
        }
}
