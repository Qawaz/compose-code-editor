import java.util.Properties

plugins {
    kotlin("multiplatform")
    id("maven-publish")
    id("org.jetbrains.compose")
    id("com.android.library")
    id("org.jetbrains.dokka")
}

group = "com.wakaztahir"
version = findProperty("version") as String

kotlin {
    android {
        publishLibraryVariants("release")
    }
    jvm("desktop") {
        compilations.all {
            kotlinOptions.jvmTarget = "11"
        }
    }
    js(IR) {
        browser()
        binaries.executable()
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                api(compose.runtime)
                api(compose.foundation)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val androidMain by getting {
            dependencies {

            }
        }
//        val androidTest by getting {
//            dependencies {
//                implementation("junit:junit:4.13.2")
//            }
//        }
        val desktopMain by getting {
            dependencies {
                api(compose.preview)
            }
        }
        val desktopTest by getting

        named("jsMain") {
            dependencies {
                api(compose.web.core)
            }
        }
    }
}

android {
    compileSdk = 32
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdk = 21
        targetSdk = 32
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}


val propertiesFile = project.rootProject.file("github.properties")
val isGithubPropAvailable = propertiesFile.exists()

if (isGithubPropAvailable) {

    val githubProperties = Properties().apply {
        propertiesFile.reader().use { load(it) }
    }

    publishing {
        repositories {
            maven {
                name = "GithubPackages"
                url = uri("https://maven.pkg.github.com/Qawaz/compose-code-editor")
                try {
                    credentials {
                        username = (githubProperties["gpr.usr"] ?: System.getenv("GPR_USER")).toString()
                        password = (githubProperties["gpr.key"] ?: System.getenv("GPR_API_KEY")).toString()
                    }
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
            }
        }
    }
}

val checkGithubTask = tasks.register("checkGithubProperties") {
    doLast {
        if (!isGithubPropAvailable) {
            error("Github properties file is not available. Throwing error.")
        }
    }
}

tasks.withType(PublishToMavenRepository::class.java).configureEach {
    dependsOn(checkGithubTask)
}
