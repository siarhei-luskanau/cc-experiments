package com.bookreads.core.network

import com.bookreads.core.network.api.BookLeaderboardApiService
import com.bookreads.core.network.ktor.BookLeaderboardApiClient
import com.bookreads.core.network.ktor.KtorBookLeaderboardApiService
import org.koin.dsl.module

val coreNetworkModule =
    module {
        single { BookLeaderboardApiClient() }
        single<BookLeaderboardApiService> { KtorBookLeaderboardApiService(client = get()) }
    }
