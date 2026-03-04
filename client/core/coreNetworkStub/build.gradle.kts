plugins {
    id("composeMultiplatformConvention")
}

kotlin {
    android.namespace = "com.bookreads.core.network.stub"

    sourceSets {
        commonMain.dependencies {
            implementation(projects.client.core.coreNetworkApi)
        }
    }
}
