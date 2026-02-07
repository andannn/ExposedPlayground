import org.gradle.api.Named
import org.gradle.api.Project
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.provider.ListProperty
import org.gradle.api.tasks.TaskProvider
import org.jetbrains.kotlin.konan.target.KonanTarget

class NativeTargetCompilation internal constructor(
    val project: Project,
    val konanTarget: KonanTarget,
    internal val compileTask: TaskProvider<ClangCompileTask>,
    internal val archiveTask: TaskProvider<ClangArchiveTask>,
    internal val linkerTask: TaskProvider<ClangLinkerTask>,
    val sources: ConfigurableFileCollection,
    val includes: ConfigurableFileCollection,
    val linkedObjects: ConfigurableFileCollection,
    @Suppress("unused")
    val linkerArgs: ListProperty<String>,
    @Suppress("unused")
    val freeArgs: ListProperty<String>,
) : Named {
    override fun getName(): String = konanTarget.name
}
