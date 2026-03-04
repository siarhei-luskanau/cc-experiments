plugins {
    id("composeMultiplatformConvention")
}

kotlin {
    android.namespace = "com.bookreads.ui.splash"
    sourceSets {
        commonMain.dependencies {
            implementation(projects.client.core.coreCommon)
            implementation(projects.client.core.corePref)
            implementation(projects.client.ui.uiCommon)
        }
    }
}
