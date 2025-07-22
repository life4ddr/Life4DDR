import org.jetbrains.compose.ExperimentalComposeLibrary

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsCompose)
}

kotlin {
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "17"
            }
        }
    }

    sourceSets {

        androidMain.dependencies {
            implementation(libs.compose.ui.tooling.preview)
            implementation(libs.compose.activity)

            implementation(libs.androidx.lifecycle.viewmodel.compose)
            implementation(libs.androidx.lifecycle.runtime.compose)
        }
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material)
            implementation(compose.ui)
            @OptIn(ExperimentalComposeLibrary::class)
            implementation(compose.components.resources)
            implementation(libs.multiplatformSettings.datastore)
            implementation(projects.shared)
        }
    }
}

android {
    namespace = "com.perrigogames.life4.android"
    compileSdk = libs.versions.compileSdk.get().toInt()

    sourceSets["main"].manifest.srcFile("src/main/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/main/res")
    sourceSets["main"].resources.srcDirs("src/commonMain/resources")

    defaultConfig {
        applicationId = "com.perrigogames.life4.android"
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = 61
        versionName = "4.0.1"
        multiDexEnabled = true
        signingConfig = signingConfigs.findByName("release")
        vectorDrawables {
            useSupportLibrary = true
        }
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildFeatures {
        compose = true
    }
    buildTypes {
        getByName("debug") {
            applicationIdSuffix = ".debug"
            addManifestPlaceholders(mapOf(Pair("providerName", "com.perrigogames.life4.fileprovider.debug")))
        }
        getByName("release") {
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
    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.8"
    }
    dependencies {
        debugImplementation(libs.compose.ui.tooling)
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
    implementation(libs.coil)
}
