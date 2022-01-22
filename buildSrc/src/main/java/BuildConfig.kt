object BuildConfig {
    object Info {
        const val group = "com.wakaztahir"
        const val version = "3.0.3"
        const val versionCode = 3

        const val ComposeVersion = "1.0.1"
        const val KotlinVersion = "1.6.10"
    }

    object Android {
        const val minSdkVersion = 21
        const val compileSdkVersion = 31
        const val targetSdkVersion = 31
    }

    object Dependencies {
        object Compose {
            val runtime get() = "org.jetbrains.compose.runtime:runtime:${Info.ComposeVersion}"
            val foundation get() = "org.jetbrains.compose.foundation:foundation:${Info.ComposeVersion}"
            val material get() = "org.jetbrains.compose.material:material:${Info.ComposeVersion}"
        }
    }
}