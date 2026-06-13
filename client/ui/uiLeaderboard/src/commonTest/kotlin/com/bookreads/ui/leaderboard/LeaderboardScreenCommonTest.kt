package com.bookreads.ui.leaderboard

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.printToLog
import androidx.compose.ui.test.v2.runComposeUiTest
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class LeaderboardScreenCommonTest {
    @Test
    fun simpleCheck() =
        runComposeUiTest {
            setContent { LeaderboardScreenPreview() }
            waitForIdle()
            awaitIdle()
            onRoot().printToLog("StartTag")
            onNodeWithText("Leaderboard").assertIsDisplayed()
        }
}
