import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.provider.Property
import org.gradle.api.services.ServiceReference
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.TaskProvider
import org.gradle.workers.WorkerExecutor
import org.jetbrains.kotlin.konan.target.KonanTarget
import javax.inject.Inject

fun Project.createRunSomethingTest(): TaskProvider<TestTask> =
    project.tasks.register("runSomething", TestTask::class.java) {
        usesService(KonanBuildService.obtain(project))
    }

abstract class TestTask
    @Inject
    constructor(
        private val workerExecutor: WorkerExecutor,
    ) : DefaultTask() {
        init {
            description =
                "for study api of gradle"
            group = "Build"
        }

        @get:ServiceReference(KonanBuildService.KEY)
        abstract val myService: Property<KonanBuildService>

        @TaskAction
        fun run() {
            println("JQN")
        }
    }
