package com.bookreads.di

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.printToLog
import androidx.compose.ui.test.runComposeUiTest
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class KoinAppCommonTest {
    @Test
    fun simpleCheck() =
        runComposeUiTest {
            setContent {
                KoinApp()
            }
            onRoot().printToLog("StartTag")
            onNodeWithText("Main").assertIsDisplayed()
        }
}
