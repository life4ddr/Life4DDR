plugins {
    kotlin("multiplatform")
    kotlin("native.cocoapods")
    kotlin("plugin.serialization")
    id("com.android.library")
    id("com.squareup.sqldelight")
    id("dev.icerock.mobile.multiplatform-resources")
}

android {
    compileSdk = libs.versions.compileSdk.get().toInt()
    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }

    lint {
        warningsAsErrors = true
        abortOnError = true
    }
    namespace = "com.perrigogames.life4"
}

version = 1.2

kotlin {
    jvmToolchain(17)
    android()
    ios()
    // Note: iosSimulatorArm64 target requires that all dependencies have M1 support
    iosSimulatorArm64()

    sourceSets {
        all {
            languageSettings.apply {
                optIn("kotlin.RequiresOptIn")
                optIn("kotlinx.coroutines.ExperimentalCoroutinesApi")
                optIn("kotlin.time.ExperimentalTime")
            }
        }
        val commonMain by getting {
            dependencies {
                implementation(libs.koin.core)
                implementation(libs.coroutines.core)
                implementation(libs.sqlDelight.coroutinesExt)
                implementation(libs.bundles.ktor.common)
                implementation(libs.touchlab.stately)
                implementation(libs.multiplatformSettings.common)
                implementation(libs.multiplatformSettings.coroutines)
                implementation(libs.kotlinx.dateTime)
                implementation(libs.bundles.reaktive.common)
                api(libs.touchlab.kermit)
                api(libs.bundles.moko.mvvm.common)
                api(libs.bundles.moko.resources.common)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(libs.bundles.shared.commonTest)
            }
        }
        val androidMain by getting {
            dependencies {
                implementation(libs.androidx.lifecycle.viewmodel)
                implementation(libs.sqlDelight.android)
                implementation(libs.ktor.client.okHttp)
                implementation(libs.moko.mvvm.android.livedata.compose)
                implementation(libs.moko.mvvm.android.livedata.glide)
                implementation(libs.multiplatformSettings.datastore)
                implementation(libs.androidx.datastore)
            }
        }
        val androidUnitTest by getting {
            dependencies {
                implementation(libs.bundles.shared.androidTest)
            }
        }
        val iosMain by getting {
            dependencies {
                implementation(libs.sqlDelight.native)
                implementation(libs.ktor.client.ios)
            }
        }
        val iosTest by getting
        val iosSimulatorArm64Main by getting {
            dependsOn(iosMain)
        }
        val iosSimulatorArm64Test by getting {
            dependsOn(iosTest)
        }
    }

    sourceSets.matching { it.name.endsWith("Test") }
        .configureEach {
            languageSettings.optIn("kotlin.time.ExperimentalTime")
        }

    cocoapods {
        summary = "Common library for LIFE4DDR logic"
        homepage = "https://github.com/PerrigoGames/Life4DDR"
        framework {
            isStatic = false // SwiftUI preview requires dynamic framework
            linkerOpts("-lsqlite3")
        }
        ios.deploymentTarget = "12.4"
        podfile = project.file("../ios/Podfile")
    }
}

sqldelight {
    database("Life4Db") {
        packageName = "com.perrigogames.life4"
        dialect = "sqlite:3.24"
    }
}

multiplatformResources {
    multiplatformResourcesPackage = "com.perrigogames.life4"
//    multiplatformResourcesVisibility = MRVisibility.Internal // optional, default Public
}
