plugins {
    id("org.jetbrains.compose") version "1.0.1"
    id("com.android.application")
    kotlin("android")
}

group = BuildConfig.Info.group
version = BuildConfig.Info.version

repositories {
    jcenter()
}

dependencies {
    implementation(project(":common"))
    implementation("androidx.activity:activity-compose:1.3.0")
}

android {
    compileSdkVersion(BuildConfig.Android.compileSdkVersion)
    defaultConfig {
        applicationId = "com.wakaztahir.android"
        minSdkVersion(BuildConfig.Android.minSdkVersion)
        targetSdkVersion(BuildConfig.Android.targetSdkVersion)
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