import org.gradle.api.Project
import org.gradle.api.provider.Provider
import org.gradle.api.services.BuildService
import org.gradle.api.services.BuildServiceParameters

// 1. 定义参数 (如果不需要参数，可以用 BuildServiceParameters.None)
interface CountServiceParams : BuildServiceParameters {
    // var initialValue: Property<Int>
}

// 2. 定义 Service 类
abstract class CountService :
    BuildService<CountServiceParams>,
    AutoCloseable {
    private var counter = 0

    @Synchronized
    fun increment(): Int {
        counter++
        println("BuildService: 当前计数 -> $counter")
        return counter
    }

    // 构建结束时自动调用
    override fun close() {
        println("BuildService: 服务关闭，最终计数 -> $counter")
    }

    companion object {
        const val KEY = "Counter service"

        fun obtain(project: Project): Provider<CountService> =
            project.gradle.sharedServices.registerIfAbsent(KEY, CountService::class.java) {
            }
    }
}
