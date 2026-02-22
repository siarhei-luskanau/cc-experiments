plugins {
    id("composeMultiplatformConvention")
}

kotlin {
    androidLibrary.namespace = "com.bookreads.core.network.stub"

    sourceSets {
        commonMain.dependencies {
            implementation(projects.client.core.coreNetworkApi)
        }
    }
}
