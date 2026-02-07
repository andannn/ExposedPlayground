import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.create

class TestPlugin2 : Plugin<Project> {
    override fun apply(project: Project) {
        project.extensions.create<ClangExtension>(
            ClangExtension.EXTENSION_NAME,
            project,
        )
    }
}
