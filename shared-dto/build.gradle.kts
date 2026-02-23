plugins {
    id("composeMultiplatformConvention")
    alias(libs.plugins.kotlinx.serialization)
}

group = "com.bookreads"

kotlin {
    androidLibrary.namespace = "com.bookreads.dto"
    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.serialization.json)
        }
    }
}
