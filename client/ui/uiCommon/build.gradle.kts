plugins {
    id("composeMultiplatformConvention")
}

kotlin.androidLibrary.namespace = "com.bookreads.ui.common"

compose.resources {
    publicResClass = true
    packageOfResClass = "${kotlin.androidLibrary.namespace}.resources"
    generateResClass = always
}
