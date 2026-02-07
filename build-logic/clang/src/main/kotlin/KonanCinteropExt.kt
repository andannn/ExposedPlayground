import com.android.utils.appendCapitalized
import org.gradle.api.Project
import org.gradle.api.file.RegularFile
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.TaskProvider
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeCompilation
import org.jetbrains.kotlin.konan.target.KonanTarget

/**
 * Configures a CInterop for the given [kotlinNativeCompilation]. The cinterop will be based on the
 * [cinteropName] in the project sources but will additionally include the references to the library
 * archive from the [ClangArchiveTask] so that it can be embedded in the generated klib of the
 * cinterop.
 */
internal fun MultiTargetNativeCompilation.configureCinterop(
    kotlinNativeCompilation: KotlinNativeCompilation,
    cinteropName: String = archiveName,
) {
    val kotlinNativeTarget = kotlinNativeCompilation.target
    if (!canCompileOnCurrentHost(kotlinNativeTarget.konanTarget)) {
        return
    }
    val konanTarget = kotlinNativeTarget.konanTarget
    val nativeTargetCompilation = targetProvider(konanTarget)
    println("JQN target: $konanTarget ${nativeTargetCompilation.hashCode()}")
    val taskNamePrefix = "androidXCinterop".appendCapitalized(kotlinNativeTarget.name, archiveName)
    val createDefFileTask =
        registerCreateDefFileTask(
            project = project,
            taskNamePrefix = taskNamePrefix,
            konanTarget = konanTarget,
            archiveProvider =
                nativeTargetCompilation
                    .flatMap { it.archiveTask }
                    .flatMap { it.llvmArchiveParameters.outputFile },
            cinteropName = cinteropName,
        )
    registerCInterop(
        kotlinNativeCompilation,
        cinteropName,
        createDefFileTask,
        nativeTargetCompilation,
    )
}

private fun registerCreateDefFileTask(
    project: Project,
    taskNamePrefix: String,
    konanTarget: KonanTarget,
    archiveProvider: Provider<RegularFile>,
    cinteropName: String,
) = project.tasks.register(
    taskNamePrefix.appendCapitalized("createDefFileFor", konanTarget.name),
    CreateDefFileWithLibraryPathTask::class.java,
) {
    objectFile.set(archiveProvider)
    target.set(
        project.layout.buildDirectory.file(
            "cinteropDefFiles/$taskNamePrefix/${konanTarget.name}/$cinteropName.def",
        ),
    )
    original.set(
        project.layout.projectDirectory.file("src/nativeInterop/cinterop/$cinteropName.def"),
    )
    projectDir.set(project.layout.projectDirectory)
}

private fun registerCInterop(
    kotlinNativeCompilation: KotlinNativeCompilation,
    cinteropName: String,
    createDefFileTask: TaskProvider<CreateDefFileWithLibraryPathTask>,
    nativeTargetCompilation: Provider<NativeTargetCompilation>? = null,
) {
    kotlinNativeCompilation.cinterops.register(cinteropName) {
        definitionFile.set(createDefFileTask.flatMap { it.target })
        nativeTargetCompilation?.let { nativeTargetCompilation ->
            includeDirs(
                nativeTargetCompilation
                    .flatMap { it.compileTask }
                    .map { it.clangParameters.includes },
            )
        }
    }
}
