import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
}

group = "org.example"
version = "1.0-SNAPSHOT"

kotlin {
    macosArm64()
    jvm {
        compilations.all {
            compileTaskProvider.configure {
                compilerOptions {
                    jvmTarget.set(JvmTarget.JVM_17)
                }
            }
        }
    }
    sourceSets {
        jvmMain.dependencies {
            implementation(libs.exposed.core)
            implementation(libs.exposed.jdbc)
            implementation(libs.exposed.java.time)

            implementation("com.h2database:h2:2.2.224")
            implementation("org.slf4j:slf4j-nop:1.7.30")
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.10.2")
        }
    }
}
