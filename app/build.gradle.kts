plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    namespace = "com.fathan.ironpulse"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.aistudio.ironpulse.k7m2xp"
        minSdk = 24
        targetSdk = 36
        versionCode = 2
        versionName = "2.0.0-native"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
}
