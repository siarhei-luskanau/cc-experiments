plugins {
    id("composeMultiplatformConvention")
}

kotlin.android.namespace = "com.bookreads.ui.common"

compose.resources {
    publicResClass = true
    packageOfResClass = "${kotlin.android.namespace}.resources"
    generateResClass = always
}
