package com.bookreads.core.network

import com.bookreads.core.network.api.BookLeaderboardApiService
import com.bookreads.core.network.stub.StubBookLeaderboardApiService
import org.koin.dsl.module

val coreNetworkModule =
    module {
        single<BookLeaderboardApiService> { StubBookLeaderboardApiService() }
    }
