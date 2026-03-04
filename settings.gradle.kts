rootProject.name = "book-leaderboard"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
include(
    ":backend",
    ":client:app:androidApp",
    ":client:app:desktopApp",
    ":client:app:webApp",
    ":client:core:coreCommon",
    ":client:core:coreData",
    ":client:core:coreNetworkApi",
    ":client:core:coreNetworkKtor",
    ":client:core:coreNetworkStub",
    ":client:core:corePref",
    ":client:diApp",
    ":client:navigation",
    ":client:ui:uiCommon",
    ":client:ui:uiHome",
    ":client:ui:uiLeaderboard",
    ":client:ui:uiMain",
    ":client:ui:uiSession",
    ":client:ui:uiSplash",
    ":shared-dto",
)

pluginManagement {
    includeBuild("convention-plugin-multiplatform")
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
                includeGroupByRegex("android.*")
            }
        }
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
                includeGroupByRegex("android.*")
            }
        }
        mavenCentral()
    }
}
