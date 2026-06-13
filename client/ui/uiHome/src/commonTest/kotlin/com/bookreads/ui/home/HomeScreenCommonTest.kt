package com.bookreads.ui.home

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.printToLog
import androidx.compose.ui.test.v2.runComposeUiTest
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class HomeScreenCommonTest {
    @Test
    fun simpleCheck() =
        runComposeUiTest {
            setContent { HomeScreenPreview() }
            waitForIdle()
            awaitIdle()
            onRoot().printToLog("StartTag")
            onNodeWithText("Book Reading Leaderboard").assertIsDisplayed()
        }
}
