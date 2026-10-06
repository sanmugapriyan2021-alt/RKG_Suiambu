// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.5.2" apply false
    id("org.jetbrains.kotlin.android") version "2.0.0" apply false
    id("org.jetbrains.kotlin.plugin.compose") version "2.0.0" apply false
}

allprojects {
    val localBuildDir = File(System.getProperty("user.home"), ".gradle_builds/Blootuth/${project.name}")
    layout.buildDirectory.set(localBuildDir)
}
