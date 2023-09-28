import org.jetbrains.kotlin.config.KotlinCompilerVersion

plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
}

android {
    compileSdkVersion(Versions.compile_sdk)
    buildToolsVersion = Versions.Android.build_tools_version
    defaultConfig {
        applicationId = "com.perrigogames.life4.android"
        minSdkVersion(Versions.min_sdk)
        targetSdkVersion(Versions.target_sdk)
        versionCode = 60
        versionName = "4.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        multiDexEnabled = true
        signingConfig = signingConfigs.findByName("release")
    }
    packagingOptions {
        exclude("META-INF/*.kotlin_module")
        exclude("META-INF/DEPENDENCIES")
        exclude("META-INF/LICENSE")
        exclude("META-INF/LICENSE.txt")
        exclude("META-INF/license.txt")
        exclude("META-INF/NOTICE")
        exclude("META-INF/NOTICE.txt")
        exclude("META-INF/notice.txt")
        exclude("META-INF/ASL2.0")
    }
    buildFeatures {
        viewBinding = true
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
    lintOptions {
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
    // Disable the google services plugin for debug
//    applicationVariants.firstOrNull { it.name.contains("debug") }.let { variant ->
//        project.tasks.getByName("process" + variant.name.capitalize() + "GoogleServices").enabled = false
//    }
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
    implementation(Deps.AndroidX.lifecycle_runtime)
    implementation(Deps.AndroidX.lifecycle_viewmodel)
    implementation(Deps.AndroidX.activity)
    implementation(Deps.AndroidX.fragment)
    implementation(Deps.AndroidX.swipeRefresh)
    debugImplementation(Deps.AndroidX.fragment_testing)
    implementation(platform(Deps.Firebase.bom))
    implementation(Deps.Firebase.messaging)
    implementation(Deps.Firebase.crashlytics)
    implementation(Deps.Firebase.analytics)
    implementation(Deps.stetho)

    implementation(Deps.koinCore)
    implementation(Deps.lottie)
    implementation(Deps.eventbus)
    implementation(Deps.dexter)
    implementation(Deps.glide)
    implementation(Deps.kotlinxDateTime)

    implementation(Deps.Ktor.androidCore)
    implementation(Deps.SqlDelight.runtimeJdk)
    implementation(Deps.SqlDelight.driverAndroid)
    implementation(Deps.Coroutines.jdk)
    implementation(Deps.Coroutines.android)
    implementation(Deps.multiplatformSettings)
    implementation(Deps.koinCore)
    androidTestImplementation(Deps.AndroidXTest.junit)
    androidTestImplementation(Deps.AndroidXTest.runner)
    androidTestImplementation(Deps.AndroidXTest.rules)
    androidTestImplementation(Deps.AndroidXTest.espresso)

    kapt(Deps.glide_compiler)
}

tasks.register<Copy>("copyDataFiles") {
    from("../json")
    into("./src/main/res/raw")
}
tasks.named("preBuild") {
    dependsOn(":app:copyDataFiles")
}