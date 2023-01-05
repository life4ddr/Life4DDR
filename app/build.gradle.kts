import org.jetbrains.kotlin.config.KotlinCompilerVersion

plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
}

android {
    compileSdk = Versions.compile_sdk
    buildToolsVersion = Versions.Android.build_tools_version
    defaultConfig {
        applicationId = "com.perrigogames.life4.android"
        minSdk = Versions.min_sdk
        targetSdk = Versions.target_sdk
        versionCode = 61
        versionName = "4.0.1"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        multiDexEnabled = true
        signingConfig = signingConfigs.findByName("release")
        vectorDrawables {
            useSupportLibrary = true
        }
    }
    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
        resources.excludes.add("META-INF/*.kotlin_module")
        resources.excludes.add("META-INF/DEPENDENCIES")
        resources.excludes.add("META-INF/LICENSE")
        resources.excludes.add("META-INF/LICENSE.txt")
        resources.excludes.add("META-INF/license.txt")
        resources.excludes.add("META-INF/NOTICE")
        resources.excludes.add("META-INF/NOTICE.txt")
        resources.excludes.add("META-INF/notice.txt")
        resources.excludes.add("META-INF/ASL2.0")
    }
    buildFeatures {
        viewBinding = true
        compose = true
    }
    buildTypes {
        getByName("debug") {
            applicationIdSuffix = ".debug"
            addManifestPlaceholders(mapOf(Pair("providerName", "com.perrigogames.life4.fileprovider.debug")))
        }
        getByName("release")  {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            addManifestPlaceholders(mapOf(Pair("providerName", "com.perrigogames.life4.fileprovider")))
            signingConfig = signingConfigs.findByName("release")
        }
    }
    signingConfigs {
        create("release") {
            storeFile = file("../../keystores/life4android.jks")
            storePassword = "TdyQWzynsV7h8PW9Qrw1x6LSb8vbfbZ0"
            keyAlias = "release"
            keyPassword = "Z7SZsZoITrpVRDb2C44f24BqvqyYI94G"
        }
    }
    lint {
        isAbortOnError = false
    }
    testOptions {
        unitTests.isIncludeAndroidResources = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    composeOptions {
        kotlinCompilerExtensionVersion = Deps.AndroidX.Compose.baseVersion
    }
}

dependencies {
    implementation(kotlin("stdlib-jdk8", KotlinCompilerVersion.VERSION))
    implementation(project(":shared"))
    implementation(Deps.AndroidX.recyclerView)
    implementation(Deps.AndroidX.core_ktx)
    implementation(Deps.AndroidX.preferences)
    implementation(Deps.AndroidX.appcompat)
    implementation(Deps.AndroidX.material)
    implementation(Deps.AndroidX.constraintlayout)
    implementation(Deps.AndroidX.Lifecycle.runtime)
    implementation(Deps.AndroidX.Lifecycle.viewmodel)
    implementation(Deps.AndroidX.Lifecycle.viewmodelCompose)
    implementation(Deps.AndroidX.activity)
    implementation(Deps.AndroidX.fragment)
    implementation(Deps.AndroidX.swipeRefresh)
    implementation(Deps.AndroidX.Compose.ui)
    implementation(Deps.AndroidX.Compose.uiTooling)
    implementation(Deps.AndroidX.Compose.foundation)
    implementation(Deps.AndroidX.Compose.material)
    implementation(Deps.AndroidX.Compose.activity)
    implementation(Deps.AndroidX.Compose.constraintLayout)
    implementation(Deps.AndroidX.Compose.appCompatTheme)

//    implementation(Deps.appintro)
    implementation(Deps.koinCore)
    implementation(Deps.lottie)
    implementation(Deps.eventbus)
    implementation(Deps.dexter)
    implementation(Deps.glide)
    implementation(Deps.coil)
    implementation(Deps.kotlinxDateTime)

    implementation(Deps.SqlDelight.runtimeJdk)
    implementation(Deps.SqlDelight.android)
    implementation(Deps.multiplatformSettings)
    implementation(Deps.koinCore)
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.3.1")
    implementation("androidx.activity:activity-compose:1.3.1")
    implementation("androidx.compose.ui:ui:${rootProject.extra["compose_ui_version"]}")
    implementation("androidx.compose.ui:ui-tooling-preview:${rootProject.extra["compose_ui_version"]}")
    implementation("androidx.compose.material:material:1.1.1")
    androidTestImplementation(Deps.junit)
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:${rootProject.extra["compose_ui_version"]}")
    debugImplementation("androidx.compose.ui:ui-tooling:${rootProject.extra["compose_ui_version"]}")
    debugImplementation("androidx.compose.ui:ui-test-manifest:${rootProject.extra["compose_ui_version"]}")

    kapt(Deps.glide_compiler)
}

tasks.register<Copy>("copyDataFiles") {
    from("../json")
    into("./src/main/res/raw")
}
tasks.named("preBuild") {
    dependsOn(":app:copyDataFiles")
}