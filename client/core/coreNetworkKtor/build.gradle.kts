plugins {
    id("composeMultiplatformConvention")
    alias(libs.plugins.kotlinx.serialization)
}

kotlin {
    android.namespace = "com.bookreads.core.network.ktor"

    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.client.engine.defaults)
            implementation(libs.ktor.core)
            implementation(libs.ktor.serialization.kotlinx.json)
            implementation(projects.client.core.coreNetworkApi)
            implementation(projects.sharedDto)
        }

        jvmTest.dependencies {
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.spring.boot.starter.test)
            implementation(libs.testcontainers.postgresql)
            implementation(project(":backend"))
            implementation(project.dependencies.platform(libs.spring.boot.dependencies))
            runtimeOnly("org.junit.platform:junit-platform-launcher")
        }
    }
}

tasks.named<Test>("jvmTest") {
    useJUnitPlatform()
    maxParallelForks = 1
}
