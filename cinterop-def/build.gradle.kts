plugins {
    alias(libs.plugins.kotlinMultiplatform)
}

kotlin {
    linuxArm64 {
        compilations.getByName("main") {
            // ---> 这一段是新增的 <---
            val simplemath by cinterops.creating {
                // def 文件名 (对应 simplemath.def)
                // 默认路径就是 src/nativeInterop/cinterop
                definitionFile.set(project.file("src/nativeInterop/cinterop/simplemath.def"))
            }
        }
    }
    macosArm64 {
        compilations.getByName("main") {
            // ---> 这一段是新增的 <---
            val simplemath by cinterops.creating {
                // def 文件名 (对应 simplemath.def)
                // 默认路径就是 src/nativeInterop/cinterop
                definitionFile.set(project.file("src/nativeInterop/cinterop/simplemath.def"))
            }
        }
    }

    sourceSets {
        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.10.2")
        }
    }
}
