import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.kotlin.jvm)
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
}

dependencies {
    implementation(libs.jetbrains.compose.components.resources)
    implementation(libs.jetbrains.compose.ui)
    implementation(projects.client.diApp)
}

compose.desktop {
    application {
        mainClass = "com.bookreads.desktop.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "compose-multiplatform-template"
            packageVersion = "1.0.0"
        }
    }
}
