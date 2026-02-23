plugins {
    id("composeMultiplatformConvention")
    alias(libs.plugins.roborazzi)
}

kotlin {
    androidLibrary.namespace = "com.bookreads.di"
    sourceSets {
        commonMain.dependencies {
            implementation(projects.client.core.coreCommon)
            implementation(projects.client.core.corePref)
            implementation(projects.client.navigation)
            implementation(projects.client.ui.uiMain)
            implementation(projects.client.ui.uiSplash)
        }

        jvmMain.dependencies {
            implementation(libs.androidx.datastore.core.okio)
        }

        jvmTest.dependencies {
            implementation(libs.roborazzi.compose.desktop)
        }

        androidMain.dependencies {
            implementation(libs.androidx.datastore.core.okio)
        }

        androidHostTest.dependencies {
            implementation(libs.robolectric)
            implementation(libs.roborazzi)
            implementation(libs.roborazzi.compose)
        }

        iosMain.dependencies {
            implementation(libs.androidx.datastore.core.okio)
        }

        iosTest.dependencies {
            implementation(libs.roborazzi.compose.ios)
        }
    }
}

// Directory for reference images
roborazzi.outputDir.set(file("src/screenshots"))
