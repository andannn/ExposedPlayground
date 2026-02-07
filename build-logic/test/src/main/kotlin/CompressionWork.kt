import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.TaskAction
import org.gradle.workers.WorkAction
import org.gradle.workers.WorkParameters
import org.gradle.workers.WorkerExecutor
import javax.inject.Inject

interface CompressionParameters : WorkParameters {
    val compressionLevel: Property<Int> // 压缩等级
}

abstract class CompressionAction : WorkAction<CompressionParameters> {
    override fun execute() {
        // 1. 获取参数
        val level = parameters.compressionLevel.get()

        // 2. 模拟干活 (比如调用 Zip 库)
        println("WorkAction: 正在线程 [${Thread.currentThread().name}]  等级: $level")

        // 模拟耗时
        Thread.sleep(500)

        println("WorkAction: 完成！")
    }
}

@CacheableTask
abstract class CompressTask
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
            // 创建一个工作队列 (WorkQueue)
            // noIsolation() 表示就在当前进程跑 (最快)
            // 也可以选 processIsolation() 启动独立进程
            val workQueue = workerExecutor.noIsolation()

            // 模拟提交 5 个任务
            for (i in 1..5) {
                workQueue.submit(CompressionAction::class.java) {
                    // 配置参数
                    compressionLevel.set(9)
                }
            }

            println("Task: 我把活都分派出去了，我先撤了！(Gradle 会等待所有 Action 执行完)")
        }
    }
