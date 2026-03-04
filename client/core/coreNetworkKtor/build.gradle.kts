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
            implementation(libs.ktor.core)
            implementation(libs.ktor.serialization.kotlinx.json)
            implementation(projects.client.core.coreNetworkApi)
            implementation(projects.sharedDto)
        }

        androidMain.dependencies {
            implementation(libs.ktor.client.cio)
        }

        jvmMain.dependencies {
            implementation(libs.ktor.client.cio)
        }

        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }

        jsMain.dependencies {
            implementation(libs.ktor.client.js)
        }

        wasmJsMain.dependencies {
            implementation(libs.ktor.client.js.wasm.js)
        }

        jvmTest.dependencies {
            implementation("org.springframework.boot:spring-boot-starter-test")
            implementation("org.testcontainers:testcontainers-postgresql")
            implementation(libs.kotlinx.coroutines.test)
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
