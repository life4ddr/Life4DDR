plugins {
    kotlin("multiplatform")
    kotlin("native.cocoapods")
    id("kotlinx-serialization")
    id("com.android.library")
    id("com.squareup.sqldelight")
}

android {
    compileSdk = Versions.compile_sdk
    defaultConfig {
        minSdk = Versions.min_sdk
        targetSdk = Versions.target_sdk
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }

    lint {
        isWarningsAsErrors = true
        isAbortOnError = true
    }
}

version = 1.2

android {
    configurations {
        create("androidTestApi")
        create("androidTestDebugApi")
        create("androidTestReleaseApi")
        create("testApi")
        create("testDebugApi")
        create("testReleaseApi")
    }
}

kotlin {
    android()
    ios()
    // Note: iosSimulatorArm64 target requires that all dependencies have M1 support
    iosSimulatorArm64()

    sourceSets {
        all {
            languageSettings.apply {
                optIn("kotlin.RequiresOptIn")
                optIn("kotlinx.coroutines.ExperimentalCoroutinesApi")
            }
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(Deps.koinCore)
                implementation(Deps.Coroutines.core)
                implementation(Deps.SqlDelight.coroutinesExtensions)
                implementation(Deps.Ktor.core)
                implementation(Deps.Ktor.logging)
                implementation(Deps.Ktor.serialization)
                implementation(Deps.Ktor.contentNegotiation)
                implementation(Deps.Reaktive.base)
                implementation(Deps.Reaktive.annotations)
                implementation(Deps.Reaktive.coroutines)
                implementation(Deps.stately)
                implementation(Deps.multiplatformSettings)
                implementation(Deps.kotlinxDateTime)
                api(Deps.kermit)
                api(Deps.MokoMvvm.Common.core)
                api(Deps.MokoMvvm.Common.flow)
                api(Deps.MokoMvvm.Common.livedata)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(Deps.kotlinTest)
                implementation(Deps.multiplatformSettingsTest)
                implementation(Deps.koinTest)
                implementation(Deps.turbine)
                implementation(Deps.Coroutines.test)
                implementation(Deps.Ktor.clientMock)
                implementation(Deps.Reaktive.testing)
                implementation(Deps.MokoMvvm.commonTest)
            }
        }
        val androidMain by getting {
            dependencies {
                implementation(Deps.AndroidX.Lifecycle.viewmodel)
                implementation(Deps.SqlDelight.android)
                implementation(Deps.Ktor.okhttp)
                api(Deps.MokoMvvm.Android.livedataCompose)
                api(Deps.MokoMvvm.Android.livedataGlide)
            }
        }
        val androidTest by getting {
            dependencies {
                implementation(Deps.junit)
                implementation(Deps.Coroutines.test)
                implementation(Deps.robolectric)
            }
        }
        val iosMain by getting {
            dependencies {
                implementation(Deps.SqlDelight.native)
                implementation(Deps.Ktor.ios)
                implementation(Deps.Coroutines.core)
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
        homepage = "https://github.com/PerrigoGames/Life4DDR-Trials"
        framework {
            isStatic = false // SwiftUI preview requires dynamic framework
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
