pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/dev/")
    }
}

include(":app", ":shared", ":editor")
rootProject.name = "LIFE4DDR"

enableFeaturePreview("VERSION_CATALOGS")