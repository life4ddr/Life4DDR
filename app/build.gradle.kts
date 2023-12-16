plugins {
    id("com.android.application")
    kotlin("android")
}

android {
    namespace = "com.perrigogames.life4.android"
    compileSdk = libs.versions.compileSdk.get().toInt()
    defaultConfig {
        applicationId = "com.perrigogames.life4.android"
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
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
        // FIXME
//        resources.excludes.add("META-INF/*.kotlin_module")
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
        warningsAsErrors = false
        abortOnError = true
    }
    testOptions {
        unitTests.isIncludeAndroidResources = true
    }
    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
        freeCompilerArgs = freeCompilerArgs + "-opt-in=kotlin.RequiresOptIn"
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.composeCompiler.get()
    }
}

dependencies {
    implementation(project(":shared"))
    implementation(libs.bundles.app.ui)
    implementation(libs.multiplatformSettings.common)
    implementation(libs.multiplatformSettings.common)
    implementation(libs.kotlinx.dateTime)
    coreLibraryDesugaring(libs.android.desugaring)
    implementation(libs.koin.android)
    testImplementation(libs.junit)

    implementation(libs.lottie)
    implementation(libs.dexter)
    implementation(libs.glide)
    implementation(libs.coil)
}

tasks.register<Copy>("copyDataFiles") {
    from("../json")
    into("./src/main/res/raw")
}
tasks.named("preBuild") {
    dependsOn(":app:copyDataFiles")
}