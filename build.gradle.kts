// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {

    repositories {
        google()
        jcenter()
        maven(url = "https://maven.fabric.io/public")
    }
    dependencies {
        classpath(Deps.android_gradle_plugin)
        classpath(Deps.SqlDelight.gradle)
        classpath(Deps.xcodesync)
        classpath("org.jetbrains.kotlin:kotlin-serialization:${Versions.kotlin}")

        classpath("io.objectbox:objectbox-gradle-plugin:${Versions.objectbox}")
        classpath("com.google.gms:google-services:${Versions.googleServices}")
        classpath("io.fabric.tools:gradle:1.29.0")

        classpath(kotlin("gradle-plugin", Versions.kotlin))
        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build gradle files
    }
}

//repositories {
//    mavenCentral()
//}

allprojects {
    repositories {
        google()
        jcenter()
        mavenCentral() //FIXME
        maven(url = "https://kotlin.bintray.com/kotlinx")
        maven(url = "https://oss.sonatype.org/content/repositories/snapshots/")
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
