package com.bookreads.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.bookreads.ui.common.theme.AppTheme
import com.bookreads.ui.home.HomeScreen
import com.bookreads.ui.leaderboard.LeaderboardScreen
import com.bookreads.ui.session.SessionScreen
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
                    entry<AppRoutes.Home> {
                        HomeScreen {
                            koin.get { parametersOf(appNavigation) }
                        }
                    }
                    entry<AppRoutes.Session> {
                        SessionScreen {
                            koin.get { parametersOf(appNavigation) }
                        }
                    }
                    entry<AppRoutes.Leaderboard> {
                        LeaderboardScreen {
                            koin.get { parametersOf(appNavigation) }
                        }
                    }
                },
        )
    }

internal sealed interface AppRoutes : NavKey {
    @Serializable
    data object Splash : AppRoutes

    @Serializable
    data object Home : AppRoutes

    @Serializable
    data object Session : AppRoutes

    @Serializable
    data object Leaderboard : AppRoutes
}
