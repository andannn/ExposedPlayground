import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.TaskProvider
import org.gradle.workers.WorkerExecutor
import javax.inject.Inject

fun Project.createTestTask(): TaskProvider<TestTask> = project.tasks.register("customTestTask", TestTask::class.java)

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

        @TaskAction
        fun run() {
            print("task registered success.")
        }
    }
