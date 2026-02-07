buildscript {
    repositories {
        google()
        mavenCentral()
    }
} // Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.task.tree) apply false
}
