package com.bookreads.navigation

import androidx.navigation3.runtime.NavKey
import com.bookreads.ui.home.HomeNavigationCallback
import com.bookreads.ui.leaderboard.LeaderboardNavigationCallback
import com.bookreads.ui.session.SessionNavigationCallback
import com.bookreads.ui.splash.SplashNavigationCallback

internal class AppNavigation(
    private val backStack: MutableList<NavKey>,
) : SplashNavigationCallback,
    HomeNavigationCallback,
    SessionNavigationCallback,
    LeaderboardNavigationCallback {
    override fun goHome() {
        backStack.add(AppRoutes.Home)
        backStack.remove(AppRoutes.Splash)
    }

    override fun goSession() {
        if (!backStack.contains(AppRoutes.Session)) {
            backStack.add(AppRoutes.Session)
        }
        backStack.remove(AppRoutes.Splash)
        backStack.remove(AppRoutes.Home)
    }

    override fun goLeaderboard() {
        if (!backStack.contains(AppRoutes.Leaderboard)) {
            backStack.add(AppRoutes.Leaderboard)
        }
    }

    override fun goBack() {
        if (backStack.size > 1) {
            backStack.removeLastOrNull()
        }
    }
}
