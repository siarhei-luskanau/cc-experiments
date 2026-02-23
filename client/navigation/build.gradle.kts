plugins {
    id("composeMultiplatformConvention")
    alias(libs.plugins.kotlinx.serialization)
}

kotlin {
    androidLibrary.namespace = "com.bookreads.navigation"
    sourceSets {
        commonMain.dependencies {
            implementation(libs.jetbrains.navigation3.ui)
            implementation(libs.kotlinx.serialization.json)
            implementation(projects.client.ui.uiCommon)
            implementation(projects.client.ui.uiMain)
            implementation(projects.client.ui.uiSplash)
        }
    }
}
