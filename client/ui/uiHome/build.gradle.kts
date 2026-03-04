plugins {
    id("composeMultiplatformConvention")
    alias(libs.plugins.roborazzi)
}

kotlin {
    android.namespace = "com.bookreads.ui.home"
    sourceSets {
        commonMain.dependencies {
            implementation(projects.client.core.coreCommon)
            implementation(projects.client.core.corePref)
            implementation(projects.client.ui.uiCommon)
        }

        androidHostTest.dependencies {
            implementation(libs.robolectric)
            implementation(libs.roborazzi)
            implementation(libs.roborazzi.compose)
        }

        jvmTest.dependencies {
            implementation(libs.roborazzi.compose.desktop)
        }

        iosTest.dependencies {
            implementation(libs.roborazzi.compose.ios)
        }
    }
}

roborazzi.outputDir.set(file("src/screenshots"))
