plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinSerialization)
}

kotlin {
    androidTarget{
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
        }
    }

    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "CoreData"
            isStatic = true
        }
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":domain"))
                implementation(libs.ktor.client.core)
                implementation(libs.ktor.client.negotiation)
                implementation(libs.ktor.serialization.json)
                implementation(libs.ktor.client.logging)
                implementation(libs.koin.core)
                implementation(libs.kotlinx.serialization.json)
            }
        }
        val androidMain by getting {
            dependencies {
                implementation(libs.ktor.client.okhttp)
                implementation(libs.androidx.datastore.core)
                implementation(libs.androidx.datastore.preference)

                // Firebase dependencies
                implementation(
                    project.dependencies.platform("com.google.firebase:firebase-bom:34.3.0")
                )
                implementation(libs.firebase.auth)
                implementation(libs.firebase.crashlytics)
                implementation(libs.firebase.analytics)
                implementation(libs.firebase.perf)
                implementation(libs.google.location)
            }
        }

        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting

        val iosMain by creating {
            dependencies {
                implementation(libs.ktor.client.darwin)

                // Firebase dependencies for iOS will be added via CocoaPods
                // Add to iosApp/Podfile:
                // pod 'Firebase/Auth', '10.27.0'
                // pod 'Firebase/Crashlytics', '10.27.0'
                // pod 'Firebase/Analytics', '10.27.0'
            }
        }
    }
}

android {
    namespace = "com.creed.data"
    compileSdk = 34
    defaultConfig {
        minSdk = 24
    }
}
