import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.konan.target.KonanTarget

plugins {
    id("test.plugin2")
    id("com.dorongold.task-tree")
}

val testArtifacts by configurations.creating {
    isCanBeConsumed = true
    isCanBeResolved = false
}

clang {
    val requiredNativeTargets =
        listOf(
            KonanTarget.MACOS_ARM64,
            KonanTarget.LINUX_ARM64,
            KonanTarget.LINUX_X64,
        )

    val nativeCompilation =
        createNativeCompilation(
            "abcd",
        ) {
            configureEachTarget {
                includes.from(
                    project.layout.projectDirectory.dir("c"),
                )
                sources.from(
                    fileTree(project.layout.projectDirectory.dir("c")).matching { include("**/*.c") },
                )
            }
            configureTargets(requiredNativeTargets)
        }

    val jniCompile =
        createNativeCompilation(
            "abcdJni",
        ) {
            configureEachTarget {
                addJniHeaders()
                includes.from(
                    project.layout.projectDirectory.dir("c"),
                )
                sources.from(
                    fileTree("src/jvmAndAndroidMain/jni").matching { include("**/*.cpp") },
                )
                include(nativeCompilation)
            }

            configureTargets(requiredNativeTargets)
        }

    macosArm64()
    linuxArm64()
    linuxX64()
    jvm {
        addNativeLibrariesToResources(this, jniCompile)
    }
    androidLibrary {
        namespace = "me.andannn.bundled.c.source"
        androidResources.enable = true

        addNativeLibrariesToJniLibs(this, jniCompile)
    }

    artifacts.add(
        "testArtifacts",
        nativeCompilation.sharedArchiveOutputFor(KonanTarget.MACOS_ARM64),
    )

    targets.configureEach {
        val target = this
        if (target is KotlinNativeTarget) {
            nativeCompilation.configureTarget(target.konanTarget)
            createCinterop(
                nativeTarget = target,
                nativeCompilation,
            )
        }
    }
}
