plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    id("co.touchlab.native.cocoapods")
    id("com.android.library")
    id("com.squareup.sqldelight")
}

android {
    compileSdkVersion(Versions.compile_sdk)
    defaultConfig {
        minSdkVersion(Versions.min_sdk)
        targetSdkVersion(Versions.target_sdk)
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
}

kotlin {
    android()
//    jvm("api")
    // Revert to just ios() when gradle plugin can properly resolve it
    val onPhone = System.getenv("SDK_NAME")?.startsWith("iphoneos") ?: false
    if (onPhone) {
        iosArm64("ios")
    } else {
        iosX64("ios")
    }

    version = "1.0"

    sourceSets {
        all {
            languageSettings.apply {
                useExperimentalAnnotation("kotlin.RequiresOptIn")
                useExperimentalAnnotation("kotlinx.coroutines.ExperimentalCoroutinesApi")
            }
        }
    }

    sourceSets["commonMain"].dependencies {
        implementation(Deps.SqlDelight.runtime)
        implementation(Deps.SqlDelight.coroutinesExtensions)
        implementation(Deps.Ktor.commonCore)
        implementation(Deps.Ktor.commonJson)
        implementation(Deps.Ktor.commonLogging)
        implementation(Deps.Ktor.commonSerialization)
        implementation(Deps.Coroutines.common) {
            version {
                strictly(Versions.coroutines)
            }
        }
        implementation(Deps.stately)
        implementation(Deps.multiplatformSettings)
        implementation(Deps.koinCore)
        implementation(Deps.kotlinxDateTime)
        api(Deps.kermit)
    }

    sourceSets["commonTest"].dependencies {
        implementation(Deps.multiplatformSettingsTest)
        implementation(Deps.KotlinTest.common)
        implementation(Deps.KotlinTest.annotations)
    }

    sourceSets["androidMain"].dependencies {
        implementation(kotlin("stdlib", Versions.kotlin))
        implementation(Deps.SqlDelight.driverAndroid)
        implementation(Deps.Coroutines.android)
        implementation(Deps.Ktor.androidCore)
        implementation(project.dependencies.platform(Deps.Firebase.bom))
        implementation(Deps.Firebase.crashlytics)
    }

    sourceSets["androidTest"].dependencies {
        implementation(Deps.KotlinTest.jvm)
        implementation(Deps.KotlinTest.junit)
        implementation(Deps.AndroidXTest.core)
        implementation(Deps.AndroidXTest.junit)
        implementation(Deps.AndroidXTest.runner)
        implementation(Deps.AndroidXTest.rules)
        implementation(Deps.Coroutines.test)
        implementation(Deps.robolectric)
    }

    sourceSets["iosMain"].dependencies {
        implementation(Deps.SqlDelight.driverIos)
        implementation(Deps.Ktor.ios)
    }

    cocoapodsext {
        summary = "Common library for LIFE4DDR logic"
        homepage = "https://github.com/PerrigoGames/Life4DDR-Trials"
        framework {
            isStatic = false
            export(Deps.kermit)
            transitiveExport = true
        }
    }
}

sqldelight {
    database("Life4Db") {
        packageName = "com.perrigogames.life4"
    }
}
