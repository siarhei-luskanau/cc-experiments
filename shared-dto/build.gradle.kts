plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
}

group = "com.bookreads"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")
}
