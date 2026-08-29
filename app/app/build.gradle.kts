plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.kuabhy.pdfprint"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.kuabhy.pdfprint"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
    }
}
