import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.services.ServiceReference
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.TaskProvider
import org.gradle.workers.WorkerExecutor
import javax.inject.Inject

fun Project.createTestTask(): TaskProvider<TestTask> {
//    val a = objects.domainObjectContainer(TestSetting::class.java)
//    a.create("setting1") {
//        println("setting1 run")
//    }
//    a.create("setting2") {
//        println("setting2 run")
//    }
//    a.named("setting1") {
//        println("setting1: found $this")
//    }
//    val provider =
//        a.register("registerSetting1") {
//            println("[Lazy] setting configuration 0")
//        }
//    a.named("registerSetting1") {
//        println("[Lazy] setting configuration 1")
//    }
//    a.configureEach {
//        println("current setting: $name")
//    }
//    provider.get()
    return project.tasks.register("customTestTask", TestTask::class.java) {
        usesService(CountService.obtain(project))
    }
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

        @get:Input
        abstract val tag: Property<String>

        @get:InputFile
        @get:PathSensitive(PathSensitivity.NAME_ONLY)
        abstract val sourceFile: RegularFileProperty

        // 3. 输出文件 (Gradle 会检测这个文件是否存在)
        @get:OutputFile
        abstract val destinationFile: RegularFileProperty

        @get:ServiceReference(CountService.KEY)
        abstract val myService: Property<CountService>

        @TaskAction
        fun run() {
            val tag = tag.get()
            println("task start. ")
            println("input: ")
            println("tag: $tag")
            println("sourceFile: ${sourceFile.get()}")

            myService.get().increment()

            val input = sourceFile.get().asFile
            val content = input.readText()
            val output = destinationFile.get().asFile
            output.writeText("Tag: $tag\nProcessed Content:\n$content")

            println("task success.")
        }
    }
