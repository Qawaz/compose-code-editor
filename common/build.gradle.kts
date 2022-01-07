plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose") version "1.0.1"
    id("com.android.library")
}

group = BuildConfig.Info.group
version = BuildConfig.Info.version

kotlin {
    android()
    jvm("desktop") {
        compilations.all {
            kotlinOptions.jvmTarget = "11"
        }
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                with(BuildConfig.Dependencies.Compose){
                    api(runtime)
                    api(foundation)
                    api(material)
                }

                implementation(project(":codeeditor"))
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val androidMain by getting
        val androidTest by getting {
            dependencies {
                implementation("junit:junit:4.13")
            }
        }
        val desktopMain by getting {
            dependencies {
                api(compose.preview)
            }
        }
        val desktopTest by getting
    }
}

android {
    compileSdkVersion(BuildConfig.Android.compileSdkVersion)
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdkVersion(BuildConfig.Android.minSdkVersion)
        targetSdkVersion(BuildConfig.Android.targetSdkVersion)
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}