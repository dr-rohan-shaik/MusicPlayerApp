package com.musicapp.buildlogic

import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

class AndroidLibraryPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.pluginManager.apply("com.android.library")
        project.pluginManager.apply("org.jetbrains.kotlin.android")
        project.pluginManager.apply("org.jetbrains.kotlin.plugin.serialization")

        project.configure<LibraryExtension> {
            compileSdk = 34
            defaultConfig {
                minSdk = 24
                targetSdk = 34
            }
            kotlinOptions {
                jvmTarget = "17"
            }
        }

        project.dependencies {
            add("implementation", "androidx.core:core-ktx:1.13.1")
            add("implementation", platform("androidx.compose:compose-bom:2024.09.00"))
            add("implementation", "androidx.compose.runtime:runtime")
            add("implementation", "androidx.compose.foundation:foundation")
            add("implementation", "org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.0")
        }
    }
}