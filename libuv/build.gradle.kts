plugins {
    alias(libs.plugins.kotlinMultiplatform)
    id("com.dorongold.task-tree")
}

val compileLibuvTask = tasks.register<LibuvBuildTask>("compileLibuv") {
    sourceDir.set(layout.projectDirectory.dir("c"))
    buildDir.set(layout.buildDirectory.dir("libuv-build"))
    outputFile.set(layout.buildDirectory.file("libs/libuv.a"))
}

val copyInteropFileTask = project.tasks.register<CreateDefFileWithLibraryPathTask>("copyInteropFileTask") {
    objectFile.set(compileLibuvTask.flatMap { it.outputFile })
    target.set(
        project.layout.buildDirectory.file(
            "cinteropDefFiles/libuv/libuv.def",
        ),
    )
    original.set(
        project.layout.projectDirectory.file("src/nativeInterop/cinterop/libuv.def"),
    )
    projectDir.set(project.layout.projectDirectory)
}

kotlin {
    macosArm64().apply {
        compilations.getByName("main") {
            val libuvInterop by cinterops.creating {
                definitionFile.set(copyInteropFileTask.flatMap { it.target })
            }
        }
    }
}

abstract class LibuvBuildTask @Inject constructor(
    private val execOperations: ExecOperations
) : DefaultTask() {
    init {
        description = "Build libuv"
        group = "Build"
    }
    @get:InputDirectory
    abstract val sourceDir: DirectoryProperty

    @get:OutputDirectory
    abstract val buildDir: DirectoryProperty

    @get:OutputFile
    abstract val outputFile: RegularFileProperty

    @TaskAction
    fun build() {
        println("JQN libuv build running")
        val workingDir = buildDir.get().asFile
        if (!workingDir.exists()) workingDir.mkdirs()
        execOperations.exec {
            workingDir(workingDir)
            commandLine("/opt/homebrew/bin/cmake", sourceDir.get().asFile.absolutePath, "-DCMAKE_BUILD_TYPE=Release")
        }

        execOperations.exec {
            workingDir(workingDir)
            commandLine("/opt/homebrew/bin/cmake", "--build", ".", "--config", "Release")
        }

        val artifactName = "libuv.a"
        val foundLib = workingDir.walkTopDown().find { it.name == artifactName }
            ?: throw GradleException("找不到生成的库文件: $artifactName")

        foundLib.copyTo(outputFile.get().asFile, overwrite = true)
    }
}

@DisableCachingByDefault(because = "not worth caching, it is a copy with file modification")
abstract class CreateDefFileWithLibraryPathTask : DefaultTask() {
    @get:InputFile
    @get:PathSensitive(PathSensitivity.NONE)
    abstract val original: RegularFileProperty

    @get:InputFile
    @get:PathSensitive(PathSensitivity.NAME_ONLY)
    abstract val objectFile: RegularFileProperty

    @get:OutputFile abstract val target: RegularFileProperty

    @get:Internal abstract val projectDir: DirectoryProperty

    @TaskAction
    fun createPlatformSpecificDefFile() {
        val target = target.asFile.get()
        target.parentFile?.mkdirs()
        // use relative path to the owning project so it can be cached (as much as possible).
        // Right now,the only way to add libraryPaths/staticLibraries is the def file, which
        // resolves paths relative to the project. Once KT-62800 is fixed, we should remove this
        // task but until than, this is the only option to pass a generated so file
        val objectFileParentDir =
            objectFile.asFile
                .get()
                .parentFile
                .canonicalFile
                .relativeTo(projectDir.get().asFile.canonicalFile)
        val outputContents =
            listOf(
                original.asFile.get().readText(Charsets.UTF_8),
                "libraryPaths=\"$objectFileParentDir\"",
                "staticLibraries=" + objectFile.asFile.get().name,
            ).joinToString(System.lineSeparator())
        target.writeText(outputContents, Charsets.UTF_8)
    }
}

