package com.bookreads.di

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.bookreads.core.common.coreCommonModule
import com.bookreads.core.data.coreDataModule
import com.bookreads.core.network.coreNetworkModule
import com.bookreads.core.pref.corePrefModule
import com.bookreads.navigation.NavApp
import com.bookreads.ui.home.HomeViewModel
import com.bookreads.ui.leaderboard.LeaderboardViewModel
import com.bookreads.ui.session.SessionViewModel
import com.bookreads.ui.splash.SplashViewModel
import org.koin.compose.KoinMultiplatformApplication
import org.koin.core.module.Module
import org.koin.dsl.KoinConfiguration
import org.koin.dsl.module

@Preview
@Composable
fun KoinApp(platformModule: Module = appPlatformModule) =
    KoinMultiplatformApplication(
        config =
            KoinConfiguration {
                modules(
                    appModule,
                    platformModule,
                    coreCommonModule,
                    corePrefModule,
                    coreNetworkModule,
                    coreDataModule,
                )
            },
    ) {
        NavApp()
    }

expect val appPlatformModule: Module

val appModule by lazy {
    module {
        factory {
            SplashViewModel(
                navigationCallback = it[0],
                prefService = get(),
                dispatcherSet = get(),
            )
        }
        factory {
            HomeViewModel(
                navigationCallback = it[0],
                prefService = get(),
                localSessionStore = get(),
                userRepository = get(),
                dispatcherSet = get(),
            )
        }
        factory {
            SessionViewModel(
                navigationCallback = it[0],
                prefService = get(),
                localSessionStore = get(),
                sessionSyncService = get(),
                dispatcherSet = get(),
            )
        }
        factory {
            LeaderboardViewModel(
                navigationCallback = it[0],
                leaderboardRepository = get(),
                prefService = get(),
                dispatcherSet = get(),
            )
        }
    }
}
