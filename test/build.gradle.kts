import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
}

group = "org.example"
version = "1.0-SNAPSHOT"

kotlin {
    macosArm64()
    linuxArm64()
    jvm {
        compilations.all {
            compileTaskProvider.configure {
                compilerOptions {
                    jvmTarget.set(JvmTarget.JVM_17)
                }
            }
        }
    }
    androidLibrary {
        namespace = "me.andannn.test"
        compileSdk = 36
        withDeviceTestBuilder {
            sourceSetTreeName = "test"
        }.configure {
            instrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        }
    }
    sourceSets {
        androidMain.dependencies {
            implementation(project(":bundled-c-source"))
        }
        jvmMain.dependencies {
            implementation(project(":bundled-c-source"))

            implementation(libs.exposed.core)
            implementation(libs.exposed.jdbc)
            implementation(libs.exposed.java.time)

            implementation("com.h2database:h2:2.2.224")
            implementation("org.slf4j:slf4j-nop:1.7.30")
        }

        nativeMain.dependencies {
            implementation(project(":bundled-c-source"))
            implementation(project(":cinterop-def"))
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.10.2")
        }
        getByName("androidDeviceTest").dependencies {
            implementation(libs.androidx.test.ext.junit)
            implementation(libs.androidx.test.core.ktx)
            implementation(libs.androidx.test.runner)
        }
    }
}
