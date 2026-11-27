plugins {
    id("com.android.library") version "8.5.0"
    id("org.jetbrains.kotlin.android") version "1.9.24"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.24"
    id("org.jetbrains.kotlin.plugin.parcelize") version "1.9.24"
}

android {
    namespace = "com.musicapp.core.model"
    compileSdk = 34

    defaultConfig {
        minSdk = 24
        targetSdk = 34
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")
}