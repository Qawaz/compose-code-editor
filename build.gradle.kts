buildscript {
    repositories {
        gradlePluginPortal()
        jcenter()
        google()
        mavenCentral()
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${BuildConfig.Info.KotlinVersion}")
        classpath("com.android.tools.build:gradle:4.1.2")
    }
}

group = BuildConfig.Info.group
version = BuildConfig.Info.version

allprojects {
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://maven.pkg.jetbrains.space/public/p/compose/dev") }
    }
}