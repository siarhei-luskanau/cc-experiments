package com.bookreads.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.bookreads.ui.common.theme.AppTheme
import com.bookreads.ui.main.MainScreen
import com.bookreads.ui.splash.SplashScreen
import kotlinx.serialization.Serializable
import org.koin.compose.getKoin
import org.koin.core.parameter.parametersOf

@Preview
@Composable
fun NavApp() =
    AppTheme {
        val koin = getKoin()
        val backStack = mutableStateListOf<NavKey>(AppRoutes.Splash)
        val appNavigation = AppNavigation(backStack = backStack)
        NavDisplay(
            backStack = backStack,
            onBack = { backStack.removeLastOrNull() },
            entryProvider =
                entryProvider {
                    entry<AppRoutes.Splash> {
                        SplashScreen {
                            koin.get { parametersOf(appNavigation) }
                        }
                    }
                    entry<AppRoutes.Main> {
                        MainScreen {
                            koin.get { parametersOf(it.initArg, appNavigation) }
                        }
                    }
                },
        )
    }

internal sealed interface AppRoutes : NavKey {
    @Serializable
    data object Splash : AppRoutes

    @Serializable
    data class Main(
        val initArg: String,
    ) : AppRoutes
}
