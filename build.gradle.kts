buildscript {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}

plugins {
    kotlin("jvm").apply(false)
    kotlin("multiplatform").apply(false)
    kotlin("android").apply(false)
    id("com.android.application").apply(false)
    id("com.android.library").apply(false)
    id("org.jetbrains.compose").apply(false)
    id("org.jetbrains.dokka")
}

tasks.withType<org.jetbrains.dokka.gradle.DokkaMultiModuleTask>(){
    outputDirectory.set(rootProject.file("docs/api"))
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}