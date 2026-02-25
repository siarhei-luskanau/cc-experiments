package com.bookreads.di

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.bookreads.core.common.coreCommonModule
import com.bookreads.core.pref.corePrefModule
import com.bookreads.navigation.NavApp
import com.bookreads.ui.main.MainViewModel
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
                )
            },
    ) {
        NavApp()
    }

expect val appPlatformModule: Module

val appModule by lazy {
    module {
        factory { SplashViewModel(navigationCallback = it[0]) }
        factory {
            MainViewModel(
                initArg = it[0],
                navigationCallback = it[1],
                dispatcherSet = get(),
                prefService = get(),
            )
        }
    }
}
