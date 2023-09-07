pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/dev/")
    }
}

include(":app", ":shared", ":editor", ":remyscrape")
rootProject.name = "LIFE4DDR"

enableFeaturePreview("VERSION_CATALOGS")