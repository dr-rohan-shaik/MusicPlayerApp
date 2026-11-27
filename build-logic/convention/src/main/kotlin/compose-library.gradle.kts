package com.musicapp.buildlogic

import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

class ComposeLibraryPlugin : Plugin<Project> {
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
            buildFeatures {
                compose = true
            }
            composeOptions {
                kotlinCompilerExtensionVersion = "2024.09.00"
            }
            kotlinOptions {
                jvmTarget = "17"
                freeCompilerArgs += listOf(
                    "-Xopt-in=kotlin.RequiresOptIn",
                    "-Xopt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
                    "-Xopt-in=androidx.compose.runtime.ComposableTarget",
                    "-Xopt-in=androidx.compose.material3.ExperimentalMaterial3Api"
                )
            }
        }

        project.dependencies {
            add("implementation", "androidx.core:core-ktx:1.13.1")
            add("implementation", platform("androidx.compose:compose-bom:2024.09.00"))
            add("implementation", "androidx.compose.material3:material3")
            add("implementation", "androidx.compose.material:material-icons-extended")
            add("implementation", "androidx.lifecycle:lifecycle-viewmodel-compose:2.8.0")
            add("implementation", "androidx.lifecycle:lifecycle-runtime-compose:2.8.0")
            add("implementation", "androidx.activity:activity-compose:1.9.2")
            add("implementation", "io.coil-kt:coil-compose:2.6.0")
        }
    }
}