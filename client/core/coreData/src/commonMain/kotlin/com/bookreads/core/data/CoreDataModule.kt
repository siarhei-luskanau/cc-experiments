package com.bookreads.core.data

import org.koin.dsl.module

val coreDataModule =
    module {
        single<UserRepository> { UserRepositoryImpl(apiService = get()) }
        single<SessionRepository> { SessionRepositoryImpl(apiService = get()) }
        single<LeaderboardRepository> { LeaderboardRepositoryImpl(apiService = get()) }
        single {
            SessionSyncService(
                sessionRepository = get(),
                sessionStore = get(),
                dispatcherSet = get(),
            )
        }
    }
