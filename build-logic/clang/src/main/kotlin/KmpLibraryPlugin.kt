import com.android.build.api.dsl.KotlinMultiplatformAndroidLibraryTarget
import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.api.variant.KotlinMultiplatformAndroidComponentsExtension
import com.android.build.gradle.api.KotlinMultiplatformAndroidPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.findByType

class KmpLibraryPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val kmpExtension =
            project.extensions.create<KMPExtension>(
                KMPExtension.EXTENSION_NAME,
                project,
            )

        project.plugins.configureEach {
            when (this) {
                is KotlinMultiplatformAndroidPlugin -> {
                    configureWithKotlinMultiplatformAndroidPlugin(project, kmpExtension.agpKmpExtension)
                }
            }
        }
    }

    private fun configureWithKotlinMultiplatformAndroidPlugin(
        project: Project,
        kotlinMultiplatformAndroidTarget: KotlinMultiplatformAndroidLibraryTarget,
    ) {
        kotlinMultiplatformAndroidTarget.apply {
            compileSdk = 36
        }
    }
}

internal val Project.androidExtension: AndroidComponentsExtension<*, *, *>
    get() =
        extensions.findByType<KotlinMultiplatformAndroidComponentsExtension>()
            ?: throw IllegalArgumentException("Failed to find any registered Android extension")
