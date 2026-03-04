// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
    val kotlinVersion = "1.5.31"
    extra["kotlinVersion"] = kotlinVersion
    
    repositories {
        google()
        mavenCentral()
        maven {
            url = uri("https://plugins.gradle.org/m2/")
        }
    }
    dependencies {
        classpath("com.android.tools.build:gradle:4.2.2")
        classpath("ru.tinkoff.gradle:jarjar:1.1.0")
        classpath("gradle.plugin.com.gladed.gradle.androidgitversion:gradle-android-git-version:0.2.21")
        classpath(files("gradle-witness.jar"))
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}


allprojects {
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}


tasks.register<Delete>("clean") {
    delete(rootProject.buildDir)
}
