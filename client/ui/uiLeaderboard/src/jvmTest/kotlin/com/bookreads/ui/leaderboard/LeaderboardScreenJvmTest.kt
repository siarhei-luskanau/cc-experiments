package com.bookreads.ui.leaderboard

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.v2.runComposeUiTest
import io.github.takahirom.roborazzi.captureRoboImage
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class LeaderboardScreenJvmTest {
    @Test
    fun preview() =
        runComposeUiTest {
            setContent { LeaderboardScreenPreview() }
            waitForIdle()
            awaitIdle()
            onRoot().captureRoboImage()
        }
}
