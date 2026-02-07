import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.services.ServiceReference
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import org.gradle.workers.WorkAction
import org.gradle.workers.WorkParameters
import org.gradle.workers.WorkerExecutor
import javax.inject.Inject

@CacheableTask
abstract class ClangArchiveTask
    @Inject
    constructor(
        private val workerExecutor: WorkerExecutor,
    ) : DefaultTask() {
        init {
            description = "Combines multiple object files (.o) into an archive file (.a)."
            group = "Build"
        }

        @get:ServiceReference(KonanBuildService.KEY)
        abstract val konanBuildService: Property<KonanBuildService>

        @get:Nested
        abstract val llvmArchiveParameters: ClangArchiveParameters

        @TaskAction
        fun archive() {
            workerExecutor.noIsolation().submit(ClangArchiveWorker::class.java) {
                llvmArchiveParameter.set(llvmArchiveParameters)
                buildService.set(konanBuildService)
            }
        }
    }

abstract class ClangArchiveParameters {
    /** The target platform for the archive file. */
    @get:Input
    abstract val konanTarget: Property<SerializableKonanTarget>

    /** The list of object files that needs to be added to the archive. */
    @get:InputFiles
    @get:PathSensitive(PathSensitivity.NAME_ONLY)
    abstract val objectFiles: ConfigurableFileCollection

    /** The final output file that will include the archive of the given [objectFiles]. */
    @get:OutputFile
    abstract val outputFile: RegularFileProperty
}

private abstract class ClangArchiveWorker : WorkAction<ClangArchiveWorker.Params> {
    interface Params : WorkParameters {
        val llvmArchiveParameter: Property<ClangArchiveParameters>
        val buildService: Property<KonanBuildService>
    }

    override fun execute() {
        val buildService = parameters.buildService.get()
        buildService.archiveLibrary(parameters.llvmArchiveParameter.get())
    }
}
