/*
 * Copyright 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
