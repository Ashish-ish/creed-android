plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeCompiler)
    // Firebase plugins
    alias(libs.plugins.googleServices)
    alias(libs.plugins.firebaseCrashlytics)
    alias(libs.plugins.firebasePerf)
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
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(project(":domain"))
                api(project(":data"))
                api(project(":di"))
                implementation(project(":usecase"))
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material3)
                implementation(compose.ui)
                implementation(compose.components.resources)
                implementation(compose.components.uiToolingPreview)
                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.koin.compose)
                implementation(libs.koin.core)
                implementation(libs.kotlinx.serialization.json)
                implementation(libs.kotlinx.datetime)
            }
        }
        val androidMain by getting {
            dependencies {
                implementation(libs.ktor.client.okhttp)
                implementation(libs.moko.geo)
                implementation(libs.androidx.activity.compose)
                implementation(libs.androidx.activity.compose)
                implementation(libs.androidx.material3)
                implementation(libs.androidx.navigation.compose)
                implementation(libs.kotlinx.coroutines.android)
                implementation(libs.koin.android)
                implementation(libs.koin.androidx.compose)

                // Firebase dependencies
                implementation(
                    project.dependencies.platform("com.google.firebase:firebase-bom:34.3.0")
                )
                implementation(libs.firebase.auth)
                implementation(libs.firebase.crashlytics)
                implementation(libs.firebase.analytics)
                implementation(libs.firebase.perf)
            }
        }
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting

        val iosMain by creating {
            dependsOn(commonMain)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)

            dependencies {
                implementation(libs.ktor.client.darwin)
                implementation(libs.moko.geo)
            }
        }
    }
}

android {
    namespace = "com.creed.android"
    compileSdk = 36
    defaultConfig {
        applicationId = "com.creed.android"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
    }
}
