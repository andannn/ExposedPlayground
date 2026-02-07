import org.gradle.api.Plugin
import org.gradle.api.Project
import java.text.SimpleDateFormat
import java.util.Date

class TestPlugin : Plugin<Project> {
    override fun apply(project: Project) =
        with(project) {
        }
}

fun testPlugin() {}

fun Project.getBuildTime(): String {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    return dateFormat.format(Date())
}
