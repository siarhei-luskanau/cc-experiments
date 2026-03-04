plugins {
    id("composeMultiplatformConvention")
}

kotlin {
    android.namespace = "com.bookreads.core.data"
    sourceSets {
        commonMain.dependencies {
            implementation(projects.client.core.coreCommon)
            implementation(projects.client.core.coreNetworkApi)
            implementation(projects.client.core.corePref)
        }
    }
}
