pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven("https://dl.google.com/dl/android/maven2/")
    }

    plugins {
        id("com.android.application") version "8.5.0" apply false
        id("com.android.library") version "8.5.0" apply false
        id("org.jetbrains.kotlin.android") version "1.9.24" apply false
        id("org.jetbrains.kotlin.plugin.serialization") version "1.9.24" apply false
        id("org.jetbrains.kotlin.plugin.parcelize") version "1.9.24" apply false
        id("org.jetbrains.kotlin.kapt") version "1.9.24" apply false
    }

    resolutionStrategy {
        eachPlugin {
            if (requested.id.id == "com.google.dagger.hilt") {
                useModule("com.google.dagger:hilt-android-gradle-plugin:2.51.1")
            }
            if (requested.id.id == "com.google.dagger.hilt.android") {
                useModule("com.google.dagger:hilt-android-gradle-plugin:2.51.1")
            }
        }
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "MusicPlayerApp"

include(":app")

// Core modules
include(":core:common")
include(":core:data")
include(":core:domain")
include(":core:model")
// include(":core:testing")  // Empty module, excluded for now

// Data modules
include(":data:local")
include(":data:remote")
include(":data:repository")

// Feature modules
include(":feature:home")
include(":feature:search")
include(":feature:library")
include(":feature:playback")
include(":feature:playlist")
include(":feature:settings")
include(":feature:profile")

// Media module
include(":media")