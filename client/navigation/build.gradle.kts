plugins {
    id("composeMultiplatformConvention")
    alias(libs.plugins.kotlinx.serialization)
}

kotlin {
    android.namespace = "com.bookreads.navigation"
    sourceSets {
        commonMain.dependencies {
            implementation(libs.jetbrains.navigation3.ui)
            implementation(libs.kotlinx.serialization.json)
            implementation(projects.client.ui.uiCommon)
            implementation(projects.client.ui.uiHome)
            implementation(projects.client.ui.uiLeaderboard)
            implementation(projects.client.ui.uiSession)
            implementation(projects.client.ui.uiSplash)
        }
    }
}
