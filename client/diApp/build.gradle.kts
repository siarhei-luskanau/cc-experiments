plugins {
    id("composeMultiplatformConvention")
    alias(libs.plugins.roborazzi)
}

kotlin {
    android.namespace = "com.bookreads.di"
    sourceSets {
        commonMain.dependencies {
            implementation(projects.client.core.coreCommon)
            implementation(projects.client.core.coreData)
            implementation(projects.client.core.coreNetworkKtor)
            implementation(projects.client.core.corePref)
            implementation(projects.client.navigation)
            implementation(projects.client.ui.uiHome)
            implementation(projects.client.ui.uiLeaderboard)
            implementation(projects.client.ui.uiSession)
            implementation(projects.client.ui.uiSplash)
            implementation(libs.androidx.datastore.core.okio)
        }

        jvmTest.dependencies {
            implementation(libs.roborazzi.compose.desktop)
        }

        androidHostTest.dependencies {
            implementation(libs.robolectric)
            implementation(libs.roborazzi)
            implementation(libs.roborazzi.compose)
        }

        iosTest.dependencies {
            implementation(libs.roborazzi.compose.ios)
        }
    }
}

// Directory for reference images
roborazzi.outputDir.set(file("src/screenshots"))
