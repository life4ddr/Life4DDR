object Versions {
    const val min_sdk = 21
    const val target_sdk = 31
    const val compile_sdk = 31

    const val kotlin = "1.6.21"
    const val ktlint_gradle_plugin = "10.2.1"
    const val glide = "4.13.1"
    const val multiplatformSettings = "0.8.1"
    const val koin = "3.1.6"

    object Android {
        const val build_tools_version = "30.0.3"
        const val gradle_plugin = "7.0.3"
    }
}

object Deps {
    const val junit = "androidx.test.ext:junit-ktx:1.1.3"
    const val kermit = "co.touchlab:kermit:1.0.3"
    const val stately =  "co.touchlab:stately-common:1.2.1"

    const val kotlinxDateTime = "org.jetbrains.kotlinx:kotlinx-datetime:0.3.2"
    const val lottie = "com.airbnb.android:lottie:5.0.3"
    const val eventbus = "org.greenrobot:eventbus:3.3.1"
    const val dexter = "com.karumi:dexter:6.2.2"
    const val appintro = "com.github.AppIntro:AppIntro:6.1.0"
    const val glide = "com.github.bumptech.glide:glide:${Versions.glide}"
    const val glide_compiler = "com.github.bumptech.glide:compiler:${Versions.glide}"

    const val multiplatformSettings =  "com.russhwolf:multiplatform-settings:${Versions.multiplatformSettings}"
    const val multiplatformSettingsTest = "com.russhwolf:multiplatform-settings-test:${Versions.multiplatformSettings}"
    const val robolectric = "org.robolectric:robolectric:4.7.3"
    const val koinCore = "io.insert-koin:koin-core:${Versions.koin}"
    const val koinTest = "io.insert-koin:koin-test:${Versions.koin}"

    const val turbine = "app.cash.turbine:turbine:0.7.0"
    const val kotlinTest = "org.jetbrains.kotlin:kotlin-test:${Versions.kotlin}"

    object Gradle {
        const val android = "com.android.tools.build:gradle:${Versions.Android.gradle_plugin}"
        const val kotlinSerialization = "org.jetbrains.kotlin:kotlin-serialization:${Versions.kotlin}"
        const val ktlint = "org.jlleitschuh.gradle:ktlint-gradle:10.2.1"
        const val sqlDelight = "com.squareup.sqldelight:gradle-plugin:${SqlDelight.version}"
    }

    object AndroidX {
        const val activity = "androidx.activity:activity-ktx:1.2.0-alpha05"
        const val appcompat = "androidx.appcompat:appcompat:1.2.0"
        const val core_ktx = "androidx.core:core-ktx:1.3.2"
        const val constraintlayout = "androidx.constraintlayout:constraintlayout:2.0.4"
        const val fragment = "androidx.fragment:fragment-ktx:1.3.0-alpha05"
        const val material = "com.google.android.material:material:1.3.0"
        const val preferences = "androidx.preference:preference:1.1.1"
        const val recyclerView = "androidx.recyclerview:recyclerview:1.2.0"
        const val swipeRefresh = "androidx.swiperefreshlayout:swiperefreshlayout:1.1.0"

        object Lifecycle {
            const val version = "2.2.0"

            const val runtime = "androidx.lifecycle:lifecycle-runtime-ktx:$version"
            const val viewmodel = "androidx.lifecycle:lifecycle-viewmodel:$version"
        }

        object Compose {
            const val baseVersion = "1.2.0-beta02"

            const val ui = "androidx.compose.ui:ui:$baseVersion"
            const val uiTooling = "androidx.compose.ui:ui-tooling:$baseVersion"
            const val foundation = "androidx.compose.foundation:foundation:$baseVersion"
            const val material = "androidx.compose.material:material:$baseVersion"
            const val materialIcons = "androidx.compose.material:material-icons-core:$baseVersion"
            const val materialIconsExtended = "androidx.compose.material:material-icons-extended:$baseVersion"
            const val activity = "androidx.activity:activity-compose:1.4.0"
            const val constraintLayout = "androidx.constraintlayout:constraintlayout-compose:1.0.1"
            const val appCompatTheme = "com.google.accompanist:accompanist-appcompat-theme:0.16.0"
        }
    }

    object Coroutines {
        const val version = "1.6.1"

        const val core = "org.jetbrains.kotlinx:kotlinx-coroutines-core:$version"
        const val test = "org.jetbrains.kotlinx:kotlinx-coroutines-test:$version"
    }

    object Reaktive {
        const val version = "1.2.1"

        const val base = "com.badoo.reaktive:reaktive:$version"
        const val annotations = "com.badoo.reaktive:reaktive-annotations:$version"
        const val coroutines = "com.badoo.reaktive:coroutines-interop:$version"
        const val testing = "com.badoo.reaktive:reaktive-testing:$version"
    }

    object SqlDelight{
        const val version = "1.5.3"

        const val runtime = "com.squareup.sqldelight:runtime:$version"
        const val coroutinesExtensions = "com.squareup.sqldelight:coroutines-extensions:$version"
        const val runtimeJdk = "com.squareup.sqldelight:runtime-jvm:$version"
        const val native = "com.squareup.sqldelight:native-driver:$version"
        const val android = "com.squareup.sqldelight:android-driver:$version"
    }

    object Ktor {
        const val version = "2.0.0"

        const val core = "io.ktor:ktor-client-core:$version"
        const val logging = "io.ktor:ktor-client-logging:$version"
        const val okhttp = "io.ktor:ktor-client-okhttp:$version"
        const val ios = "io.ktor:ktor-client-ios:$version"
        const val serialization = "io.ktor:ktor-serialization-kotlinx-json:$version"
        const val contentNegotiation = "io.ktor:ktor-client-content-negotiation:$version"
        const val clientMock = "io.ktor:ktor-client-mock:$version"
    }
}
