plugins {
    id("org.jetbrains.compose") version BuildConfig.Info.ComposeVersion
    id("com.android.application")
    kotlin("android")
}

group = BuildConfig.Info.group
version = BuildConfig.Info.version

dependencies {
    implementation(project(":common"))
    implementation("androidx.activity:activity-compose:1.3.0")
}

android {
    compileSdk = BuildConfig.Android.compileSdkVersion
    defaultConfig {
        applicationId = "com.wakaztahir.android"
        minSdk = BuildConfig.Android.minSdkVersion
        targetSdk = BuildConfig.Android.targetSdkVersion
        versionCode = BuildConfig.Info.versionCode
        versionName = BuildConfig.Info.version
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
}