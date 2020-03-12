import org.gradle.api.artifacts.ExternalModuleDependency
import org.gradle.kotlin.dsl.exclude

object Versions {
    val min_sdk = 18
    val target_sdk = 28
    val compile_sdk = 28

    val kotlin = "1.3.61"
    val android_x = "1.1.0"
    val android_x_lifecycle = "2.0.0"
    val android_gradle_plugin = "3.5.0"
    val buildToolsVersion = "29.0.0"
    val googleServices = "4.3.3"
    val junit = "4.12"
    val sqlDelight = "1.2.1"
    val ktor = "1.2.6"
    val stately = "0.9.5"
    val klock = "1.7.3"
    val lottie = "3.3.0"
    val eventbus = "3.1.1"
    val dexter = "5.0.0"
    val glide = "4.9.0"
    val gson = "2.8.2"
    val retrofit = "2.6.0"
    val firebase = "16.0.9"
    val firebase_messaging = "19.0.0"
    val fabric = "2.10.0"
    val okhttp = "3.12.0"
    val objectbox = "2.4.0"
    val multiplatformSettings = "0.5"
    val coroutines = "1.3.3-native-mt"
    val koin = "3.0.1-khan-SNAPSHOT"
    val serialization = "0.14.0"
    val xcodesync = "0.2"

}

object Deps {
    val core_ktx = "androidx.core:core-ktx:${Versions.android_x}"
    val preferences_x = "androidx.preference:preference:${Versions.android_x}"
    val app_compat_x = "androidx.appcompat:appcompat:${Versions.android_x}"
    val material_x = "com.google.android.material:material:${Versions.android_x}"
    val constraintlayout = "androidx.constraintlayout:constraintlayout:${Versions.android_x}"
    val lifecycle = "androidx.lifecycle:lifecycle-extensions:${Versions.android_x_lifecycle}"
    val viewmodel = "androidx.lifecycle:lifecycle-viewmodel-ktx:${Versions.android_x_lifecycle}"
    val firebase = "com.google.firebase:firebase-core:${Versions.firebase}"
    val firebase_messaging = "com.google.firebase:firebase-messaging:${Versions.firebase_messaging}"
    val firebase_crashlytics = "com.crashlytics.sdk.android:crashlytics:${Versions.fabric}"

    val klock = "com.soywiz.korlibs.klock:klock:${Versions.klock}"
    val lottie = "com.airbnb.android:lottie:${Versions.lottie}"
    val eventbus = "org.greenrobot:eventbus:${Versions.eventbus}"
    val dexter = "com.karumi:dexter:${Versions.dexter}"
    val glide = "com.github.bumptech.glide:glide:${Versions.glide}"
    val gson = "com.google.code.gson:gson:${Versions.gson}"
    val retrofit = "com.squareup.retrofit2:retrofit:${Versions.retrofit}"
    val retrofit_gson = "com.squareup.retrofit2:converter-gson:${Versions.retrofit}"
    val retrofit_scalars = "com.squareup.retrofit2:converter-scalars:${Versions.retrofit}"
    val okhttp = "com.squareup.okhttp3:okhttp:${Versions.okhttp}"
    val okhttp_interceptor = "com.squareup.okhttp3:logging-interceptor:${Versions.okhttp}"
    val glide_compiler = "com.github.bumptech.glide:compiler:${Versions.glide}"
    val objectbox_debug = "io.objectbox:objectbox-android-objectbrowser:${Versions.objectbox}"
    val objectbox_release = "io.objectbox:objectbox-android:${Versions.objectbox}"

    val android_gradle_plugin = "com.android.tools.build:gradle:${Versions.android_gradle_plugin}"
    val junit = "junit:junit:${Versions.junit}"
    val stately =  "co.touchlab:stately:${Versions.stately}"
    val multiplatformSettings =  "com.russhwolf:multiplatform-settings:${Versions.multiplatformSettings}"
    val multiplatformSettingsTest = "com.russhwolf:multiplatform-settings-test:${Versions.multiplatformSettings}"
    val koinCore = "co.touchlab:koin-core:${Versions.koin}"
    val xcodesync = "co.touchlab:kotlinxcodesync:${Versions.xcodesync}"


    object AndroidXTest {
        val core =  "androidx.test:core:${Versions.android_x}"
        val junit =  "androidx.test.ext:junit:${Versions.android_x}"
        val runner = "androidx.test:runner:${Versions.android_x}"
        val rules = "androidx.test:rules:${Versions.android_x}"
    }

    object KotlinTest {
        val common =      "org.jetbrains.kotlin:kotlin-test-common:${Versions.kotlin}"
        val annotations = "org.jetbrains.kotlin:kotlin-test-annotations-common:${Versions.kotlin}"
        val jvm =         "org.jetbrains.kotlin:kotlin-test:${Versions.kotlin}"
        val junit =       "org.jetbrains.kotlin:kotlin-test-junit:${Versions.kotlin}"
        val reflect =     "org.jetbrains.kotlin:kotlin-reflect:${Versions.kotlin}"
    }
    object Coroutines {
        val common = "org.jetbrains.kotlinx:kotlinx-coroutines-core-common:${Versions.coroutines}"
        val jdk = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.coroutines}"
        val native = "org.jetbrains.kotlinx:kotlinx-coroutines-core-native:${Versions.coroutines}"
        val android = "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.coroutines}"
        val test = "org.jetbrains.kotlinx:kotlinx-coroutines-test:${Versions.coroutines}"
    }
    object SqlDelight{
        val gradle =        "com.squareup.sqldelight:gradle-plugin:${Versions.sqlDelight}"
        val runtime =       "com.squareup.sqldelight:runtime:${Versions.sqlDelight}"
        val runtimeJdk =    "com.squareup.sqldelight:runtime-jvm:${Versions.sqlDelight}"
        val driverIos =     "com.squareup.sqldelight:ios-driver:${Versions.sqlDelight}"
        val driverAndroid = "com.squareup.sqldelight:android-driver:${Versions.sqlDelight}"
    }
    object ktor {
        val commonCore = "io.ktor:ktor-client-core:${Versions.ktor}"
        val commonJson = "io.ktor:ktor-client-json:${Versions.ktor}"
        val jvmCore =     "io.ktor:ktor-client-core-jvm:${Versions.ktor}"
        val androidCore = "io.ktor:ktor-client-okhttp:${Versions.ktor}"
        val jvmJson =     "io.ktor:ktor-client-json-jvm:${Versions.ktor}"
        val ios =         "io.ktor:ktor-client-ios:${Versions.ktor}"
        val iosCore =     "io.ktor:ktor-client-core-native:${Versions.ktor}"
        val iosJson =     "io.ktor:ktor-client-json-native:${Versions.ktor}"
        val commonSerialization ="io.ktor:ktor-client-serialization:${Versions.ktor}"
        val androidSerialization ="io.ktor:ktor-client-serialization-jvm:${Versions.ktor}"
        val iosSerialization ="io.ktor:ktor-client-serialization-native:${Versions.ktor}"
    }
    val coroutinesExcludeNative: ExternalModuleDependency.() -> Unit = {
        exclude(group = "org.jetbrains.kotlinx", module = "kotlinx-coroutines-core-native")
    }
}
