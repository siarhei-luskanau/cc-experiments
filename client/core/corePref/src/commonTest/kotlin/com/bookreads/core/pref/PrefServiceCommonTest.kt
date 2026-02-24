package com.bookreads.core.pref

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class PrefServiceCommonTest {
    @BeforeTest
    fun beforeTest() {
        cleanUpTestStorage()
    }

    @AfterTest
    fun afterTest() {
        cleanUpTestStorage()
    }

    @Test
    fun writeAndReadKey() =
        runTest {
            val koinApplication = startKoin { modules(testPrefModule()) }
            val service = koinApplication.koin.get<PrefService>()
            assertNull(service.getKey().first())
            service.setKey("alice")
            assertEquals("alice", service.getKey().first())
            stopKoin()
        }

    @Ignore // ("There are multiple DataStores active for the same file")
    @Test
    fun persistenceAcrossKoinSessions() {
        runTest {
            val koinApplication1 = startKoin { modules(testPrefModule()) }
            koinApplication1.koin.get<PrefService>().setKey("alice")
            stopKoin()
        }
        runTest {
            val koinApplication2 = startKoin { modules(testPrefModule()) }
            val service = koinApplication2.koin.get<PrefService>()
            assertEquals("alice", service.getKey().first())
            stopKoin()
        }
    }
}
