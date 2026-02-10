import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.konan.target.KonanTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    id("com.dorongold.task-tree")
}

kotlin {
    macosArm64()
    targets.configureEach {
        val target = this
        if (target is KotlinNativeTarget) {
//            nativeCompilation.configureTarget(target.konanTarget)
//            createCinterop(
//                nativeTarget = target,
//                nativeCompilation,
//            )
        }
    }
}
