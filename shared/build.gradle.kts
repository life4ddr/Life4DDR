import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    kotlin("multiplatform")
    kotlin("native.cocoapods")
    id("kotlinx-serialization")
    id("com.android.library")
    id("com.squareup.sqldelight")
    id("co.touchlab.kotlinxcodesync")
}

android {
    compileSdkVersion(28)
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
    //Revert to just ios() when gradle plugin can properly resolve it
//    val onPhone = System.getenv("SDK_NAME")?.startsWith("iphoneos")?:false
//    if(onPhone){
//        iosArm64("ios")
//    }else{
//        iosX64("ios")
//    }
//    targets.getByName<KotlinNativeTarget>("ios").compilations["main"].kotlinOptions.freeCompilerArgs += "-Xobjc-generics"

    version = "1.0"

    sourceSets["commonMain"].dependencies {
        implementation(kotlin("stdlib-common", Versions.kotlin))
        implementation(Deps.SqlDelight.runtime)
        implementation(Deps.ktor.commonCore)
        implementation(Deps.ktor.commonJson)
        implementation(Deps.ktor.commonSerialization)
        implementation(Deps.Coroutines.common)
        implementation(Deps.stately)
        implementation(Deps.multiplatformSettings)
        implementation(Deps.koinCore)
        implementation(Deps.klock)
    }

    sourceSets["commonTest"].dependencies {
        implementation(Deps.multiplatformSettingsTest)
        implementation(Deps.SqlDelight.runtime)
        implementation(Deps.KotlinTest.common)
        implementation(Deps.KotlinTest.annotations)
        implementation(Deps.Coroutines.jdk)
        implementation(Deps.Coroutines.common)
        implementation(Deps.Coroutines.test)
    }

    sourceSets["androidMain"].dependencies {
        implementation(kotlin("stdlib", Versions.kotlin))
        implementation(Deps.SqlDelight.driverAndroid)
        implementation(Deps.ktor.jvmCore)
        implementation(Deps.ktor.jvmJson)
        implementation(Deps.Coroutines.jdk)
        implementation(Deps.Coroutines.android)
        implementation(Deps.ktor.androidSerialization)
        implementation(Deps.firebase_crashlytics)
    }

    sourceSets["androidTest"].dependencies {
        implementation(Deps.KotlinTest.jvm)
        implementation(Deps.KotlinTest.junit)
        implementation(Deps.Coroutines.jdk)
        implementation(Deps.AndroidXTest.core)
        implementation(Deps.AndroidXTest.junit)
        implementation(Deps.AndroidXTest.runner)
        implementation(Deps.AndroidXTest.rules)
        implementation("org.robolectric:robolectric:4.3")
    }

//    sourceSets["iosMain"].dependencies {
//        implementation(Deps.SqlDelight.driverIos)
//        implementation(Deps.ktor.ios, Deps.coroutinesExcludeNative)
//        implementation(Deps.ktor.iosCore, Deps.coroutinesExcludeNative)
//        implementation(Deps.ktor.iosJson, Deps.coroutinesExcludeNative)
//        implementation(Deps.Coroutines.native)
//        implementation(Deps.ktor.iosSerialization)
//    }

    cocoapods {
        summary = "Common library for LIFE4DDR logic"
        homepage = "https://github.com/PerrigoGames/Life4DDR-Trials"
    }

    xcodeSync {
        projectPath = "../ios/LIFE4.xcodeproj"
        target = "LIFE4"
    }
}

sqldelight {
    database("Life4Db") {
        packageName = "com.perrigogames.life4"
    }
}

val iOSTest: Task by tasks.creating {
    val device = project.findProperty("iosDevice")?.toString() ?: "iPhone 8"
    dependsOn("linkDebugTestIos")
    group = JavaBasePlugin.VERIFICATION_GROUP
    description = "Runs tests for target 'ios' on an iOS simulator"

    doLast {
        val binary = kotlin.targets.getByName<KotlinNativeTarget>("ios").binaries.getTest("DEBUG").outputFile
        exec {
            commandLine("xcrun", "simctl", "spawn", "--standalone",device, binary.absolutePath)
        }
    }
}
