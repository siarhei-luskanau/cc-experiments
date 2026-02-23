plugins {
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.kotlin.multiplatform)
}

kotlin {
    jvmToolchain(
        libs.versions.javaVersion
            .get()
            .toInt(),
    )

    compilerOptions {
        freeCompilerArgs.add("-Xexplicit-backing-fields")
    }

    js {
        browser()
        binaries.executable()
    }

    wasmJs {
        browser()
        binaries.executable()
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.jetbrains.compose.ui)
            implementation(projects.client.diApp)
        }
    }
}
