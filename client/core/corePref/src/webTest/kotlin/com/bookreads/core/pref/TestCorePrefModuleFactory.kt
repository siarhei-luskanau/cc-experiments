package com.bookreads.core.pref

import kotlinx.browser.localStorage
import org.koin.core.module.Module

actual fun testPrefModule(): Module = corePrefModule

actual fun cleanUpTestStorage() {
    localStorage.clear()
}
