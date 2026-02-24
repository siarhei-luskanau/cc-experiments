plugins {
    id("composeMultiplatformConvention")
    alias(libs.plugins.kotlinx.serialization)
}

kotlin {
    androidLibrary.namespace = "com.bookreads.core.pref"
    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.serialization.json)
        }

        commonTest.dependencies {
            implementation(libs.kotlinx.coroutines.test)
        }

        jvmMain.dependencies {
            implementation(libs.androidx.datastore.core.okio)
        }

        androidMain.dependencies {
            implementation(libs.androidx.datastore.core.okio)
        }

        iosMain.dependencies {
            implementation(libs.androidx.datastore.core.okio)
        }

        webMain.dependencies {
            implementation(libs.kotlinx.browser)
        }
    }
}
