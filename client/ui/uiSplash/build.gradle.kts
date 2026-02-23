plugins {
    id("composeMultiplatformConvention")
}

kotlin {
    androidLibrary.namespace = "com.bookreads.ui.splash"
    sourceSets {
        commonMain.dependencies {
            implementation(projects.client.core.coreCommon)
            implementation(projects.client.ui.uiCommon)
        }
    }
}
