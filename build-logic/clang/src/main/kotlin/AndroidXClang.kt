import org.gradle.api.Action
import org.gradle.api.Project
import org.jetbrains.kotlin.konan.target.LinkerOutputKind

class AndroidXClang(
    val project: Project,
) {
    private val multiTargetNativeCompilations = mutableMapOf<String, MultiTargetNativeCompilation>()

    fun createNativeCompilation(
        archiveName: String,
        outputKind: LinkerOutputKind,
        configure: Action<MultiTargetNativeCompilation>,
    ): MultiTargetNativeCompilation {
        val multiTargetNativeCompilation =
            multiTargetNativeCompilations.getOrPut(archiveName) {
                MultiTargetNativeCompilation(
                    project = project,
                    archiveName = archiveName,
                    outputKind = outputKind,
                )
            }
        configure.execute(multiTargetNativeCompilation)
        return multiTargetNativeCompilation
    }
}
