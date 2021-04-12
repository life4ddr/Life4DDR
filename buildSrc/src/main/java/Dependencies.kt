import org.gradle.api.artifacts.ExternalModuleDependency
import org.gradle.kotlin.dsl.exclude

object Versions {
    const val min_sdk = 18
    const val target_sdk = 30
    const val compile_sdk = 30

    const val kotlin = "1.3.61"
    const val android_x = "1.1.0"
    const val android_x_constraint = "1.1.3"
    const val android_x_lifecycle = "2.1.0"
    const val android_x_activity = "1.2.0-alpha05"
    const val android_x_fragment = "1.3.0-alpha05"
    const val android_gradle_plugin = "4.0.0"
    const val buildToolsVersion = "29.0.2"
    const val espresso = "3.1.0"
    const val googleServices = "4.3.5"
    const val sqlDelight = "1.2.1"
    const val ktor = "1.2.6"
    const val ktorm = "2.5"
    const val sql_connector = "8.0.18"
    const val stately = "0.9.5"
    const val klock = "1.8.4"
    const val lottie = "3.3.0"
    const val eventbus = "3.1.1"
    const val dexter = "5.0.0"
    const val glide = "4.9.0"
    const val gson = "2.8.2"
    const val firebase = "27.0.0"
    const val stetho = "1.5.1"
    const val multiplatformSettings = "0.5"
    const val coroutines = "1.3.3-native-mt"
    const val koin = "3.0.1-khan-SNAPSHOT"
    const val xcodesync = "0.2"
}

object Deps {
    const val core_ktx = "androidx.core:core-ktx:${Versions.android_x}"
    const val preferences_x = "androidx.preference:preference:${Versions.android_x}"
    const val app_compat_x = "androidx.appcompat:appcompat:${Versions.android_x}"
    const val material_x = "com.google.android.material:material:${Versions.android_x}"
    const val constraintlayout = "androidx.constraintlayout:constraintlayout:${Versions.android_x_constraint}"
    const val lifecycle = "androidx.lifecycle:lifecycle-extensions:${Versions.android_x_lifecycle}"
    const val recyclerview = "androidx.recyclerview:recyclerview:${Versions.android_x}"
    const val viewmodel = "androidx.lifecycle:lifecycle-viewmodel-ktx:${Versions.android_x_lifecycle}"
    const val activity = "androidx.activity:activity-ktx:${Versions.android_x_activity}"
    const val fragment = "androidx.fragment:fragment-ktx:${Versions.android_x_fragment}"
    const val fragment_testing = "androidx.fragment:fragment-testing:${Versions.android_x_fragment}"
    const val firebase = "com.google.firebase:firebase-bom:${Versions.firebase}"
    const val firebase_messaging = "com.google.firebase:firebase-messaging-ktx"
    const val firebase_crashlytics = "com.google.firebase:firebase-crashlytics-ktx"
    const val firebase_analytics = "com.google.firebase:firebase-analytics-ktx"

    const val stetho = "com.facebook.stetho:stetho:${Versions.stetho}"

    const val klock = "com.soywiz.korlibs.klock:klock:${Versions.klock}"
    const val lottie = "com.airbnb.android:lottie:${Versions.lottie}"
    const val eventbus = "org.greenrobot:eventbus:${Versions.eventbus}"
    const val dexter = "com.karumi:dexter:${Versions.dexter}"
    const val glide = "com.github.bumptech.glide:glide:${Versions.glide}"
    const val gson = "com.google.code.gson:gson:${Versions.gson}"
    const val glide_compiler = "com.github.bumptech.glide:compiler:${Versions.glide}"

    const val android_gradle_plugin = "com.android.tools.build:gradle:${Versions.android_gradle_plugin}"
    const val stately =  "co.touchlab:stately:${Versions.stately}"
    const val multiplatformSettings =  "com.russhwolf:multiplatform-settings:${Versions.multiplatformSettings}"
    const val multiplatformSettingsTest = "com.russhwolf:multiplatform-settings-test:${Versions.multiplatformSettings}"
    const val koinCore = "co.touchlab:koin-core:${Versions.koin}"
    const val xcodesync = "co.touchlab:kotlinxcodesync:${Versions.xcodesync}"

    object AndroidXTest {
        const val core =  "androidx.test:core:${Versions.android_x}"
        const val junit =  "androidx.test.ext:junit:${Versions.android_x}"
        const val runner = "androidx.test:runner:${Versions.android_x}"
        const val rules = "androidx.test:rules:${Versions.android_x}"
        const val espresso = "androidx.test.espresso:espresso-core:${Versions.espresso}"
    }

    object KotlinTest {
        const val common =      "org.jetbrains.kotlin:kotlin-test-common:${Versions.kotlin}"
        const val annotations = "org.jetbrains.kotlin:kotlin-test-annotations-common:${Versions.kotlin}"
        const val jvm =         "org.jetbrains.kotlin:kotlin-test:${Versions.kotlin}"
        const val junit =       "org.jetbrains.kotlin:kotlin-test-junit:${Versions.kotlin}"
        const val reflect =     "org.jetbrains.kotlin:kotlin-reflect:${Versions.kotlin}"
    }
    object Coroutines {
        const val common = "org.jetbrains.kotlinx:kotlinx-coroutines-core-common:${Versions.coroutines}"
        const val jdk = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.coroutines}"
        const val native = "org.jetbrains.kotlinx:kotlinx-coroutines-core-native:${Versions.coroutines}"
        const val android = "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.coroutines}"
        const val test = "org.jetbrains.kotlinx:kotlinx-coroutines-test:${Versions.coroutines}"
    }
    object SqlDelight{
        const val gradle =        "com.squareup.sqldelight:gradle-plugin:${Versions.sqlDelight}"
        const val runtime =       "com.squareup.sqldelight:runtime:${Versions.sqlDelight}"
        const val runtimeJdk =    "com.squareup.sqldelight:runtime-jvm:${Versions.sqlDelight}"
        const val driverIos =     "com.squareup.sqldelight:ios-driver:${Versions.sqlDelight}"
        const val driverAndroid = "com.squareup.sqldelight:android-driver:${Versions.sqlDelight}"
        const val driverJvm =     "com.squareup.sqldelight:sqlite-driver:${Versions.sqlDelight}"
    }
    object ktor {
        const val commonCore = "io.ktor:ktor-client-core:${Versions.ktor}"
        const val commonJson = "io.ktor:ktor-client-json:${Versions.ktor}"
        const val jvmCore =     "io.ktor:ktor-client-core-jvm:${Versions.ktor}"
        const val androidCore = "io.ktor:ktor-client-okhttp:${Versions.ktor}"
        const val jvmJson =     "io.ktor:ktor-client-json-jvm:${Versions.ktor}"
        const val ios =         "io.ktor:ktor-client-ios:${Versions.ktor}"
        const val iosCore =     "io.ktor:ktor-client-core-native:${Versions.ktor}"
        const val iosJson =     "io.ktor:ktor-client-json-native:${Versions.ktor}"
        const val commonSerialization ="io.ktor:ktor-client-serialization:${Versions.ktor}"
        const val androidSerialization ="io.ktor:ktor-client-serialization-jvm:${Versions.ktor}"
        const val iosSerialization ="io.ktor:ktor-client-serialization-native:${Versions.ktor}"
    }
    val coroutinesExcludeNative: ExternalModuleDependency.() -> Unit = {
        exclude(group = "org.jetbrains.kotlinx", module = "kotlinx-coroutines-core-native")
    }
}
