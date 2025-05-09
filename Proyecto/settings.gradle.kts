pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
    plugins {
        id("com.android.application")      version "8.10.0" apply false
        id("org.jetbrains.kotlin.android") version "1.8.22"   apply false
        id("androidx.navigation.safeargs") version "2.5.3"      apply false
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}

rootProject.name = "FacilGimApp"
include(":app")
