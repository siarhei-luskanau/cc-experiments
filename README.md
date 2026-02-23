# book-leaderboard

### Backend
Run the desktop application: `./gradlew :backend:bootRun`  
Run the desktop **hot reload** application: `./gradlew :backend:assemble :backend:build :backend:test`

### Android
To run the application on android device/emulator:
- open project in Android Studio and run imported android run configuration

To build the application bundle:
- run `./gradlew :client:app:androidApp:assembleDebug :client:app:androidApp:assembleRelease`
- find `.apk` file in `/client/app/androidApp/build/outputs/apk/debug/androidApp-debug.apk`
- Run android UI tests on the virtual device: `./gradlew managedVirtualDeviceDebugAndroidTest managedVirtualDeviceAndroidDeviceTest -Pandroid.testoptions.manageddevices.emulator.gpu=swiftshader_indirect`

### Desktop
Run the desktop application: `./gradlew :client:app:desktopApp:run`  
Run the desktop **hot reload** application: `./gradlew :client:app:desktopApp:hotRun --auto`
Run desktop UI tests: `./gradlew jvmTest`

### iOS
To run the application on iPhone device/simulator:
- Open `iosApp/iosApp.xcproject` in Xcode and run standard configuration
- Or use [Kotlin Multiplatform Mobile plugin](https://plugins.jetbrains.com/plugin/14936-kotlin-multiplatform-mobile) for Android Studio
- Run iOS simulator UI tests: `./gradlew iosSimulatorArm64Test`

### Web Distribution
Build web distribution: `./gradlew :client:app:webApp:composeCompatibilityBrowserDistribution`  
Deploy a dir `client/app/webApp/build/dist/composeWebCompatibility/productionExecutable` to a web server

### JS Browser
Run the browser application: `./gradlew :client:app:webApp:jsBrowserDevelopmentRun`

### Wasm Browser
Run the browser application: `./gradlew :client:app:webApp:wasmJsBrowserDevelopmentRun`  
Run browser UI tests: `./gradlew :client:app:webApp:wasmJsBrowserTest`

### Code style and linting
- launch ktlintFormat: `./gradlew ktlintFormat`
- launch ktlint, detekt and lint checks: `./gradlew ktlintCheck detekt lint`

### Screenshot Testing
Record a screenshot: `./gradlew recordRoborazzi`
