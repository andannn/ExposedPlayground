import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.konan.target.KonanTarget
import org.jetbrains.kotlin.konan.target.LinkerOutputKind

plugins {
//    id("test.plugin")
    id("test.plugin2")
    id("com.dorongold.task-tree")
}

val testArtifacts by configurations.creating {
    isCanBeConsumed = true
    isCanBeResolved = false
}

clang {
    macosArm64()
    linuxArm64()

    val requiredNativeTargets =
        listOf(
            KonanTarget.MACOS_ARM64,
            KonanTarget.LINUX_ARM64,
        )

    val clangCompile =
        createNativeCompilation(
            "abcd",
        ) {
            configureEachTarget {
                includes.from(
                    project.layout.projectDirectory.dir("c"),
                )
                sources.from(
                    project.layout.projectDirectory.dir("c"),
                )
            }
            configureTargets(requiredNativeTargets)
        }

    artifacts.add(
        "testArtifacts",
        clangCompile.sharedArchiveOutputFor(KonanTarget.MACOS_ARM64),
    )

    targets.configureEach {
        val target = this
        if (target is KotlinNativeTarget) {
            clangCompile.configureTarget(target.konanTarget)
            // Create cinterop code for this native target from the SQLite compilation.
            createCinterop(
                nativeTarget = target,
                clangCompile,
            )
        }
    }
}
