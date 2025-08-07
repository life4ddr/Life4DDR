buildscript {
    repositories {
        gradlePluginPortal()
    }

    dependencies {
        classpath(libs.resources.generator)
        classpath(libs.moko.kswift.gradle.plugin)
    }
}

plugins {
    // this is necessary to avoid the plugins to be loaded multiple times
    // in each subproject's classloader
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.gradleVersions)
    alias(libs.plugins.composeMultiplatform) apply false
    alias(libs.plugins.kotlinSerialization) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.ktlint) apply false
    alias(libs.plugins.sqlDelight) apply false
}

allprojects {
    repositories {
        mavenCentral()
        google()
        maven("https://androidx.dev/storage/compose-compiler/repository/")
        maven("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/dev/")
    }
}

subprojects {
    // TODO libs doesn't resolve if we do this
    // apply(plugin = libs.plugins.ktlint.get().pluginId)
    apply(plugin = "org.jlleitschuh.gradle.ktlint")

    configure<org.jlleitschuh.gradle.ktlint.KtlintExtension> {
        enableExperimentalRules.set(true)
        verbose.set(true)
        filter {
            exclude { it.file.path.contains("build/") }
        }
    }

    afterEvaluate {
        tasks.named("check").configure {
            dependsOn(tasks.getByName("ktlintCheck"))
        }
    }
}

tasks.register<Copy>("copyDataFiles") {
    from("./json")
    into("./shared/src/commonMain/resources/MR/files")
}
