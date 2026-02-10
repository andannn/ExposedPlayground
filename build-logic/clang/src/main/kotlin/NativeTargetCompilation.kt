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

import org.gradle.api.Named
import org.gradle.api.Project
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.provider.ListProperty
import org.gradle.api.tasks.TaskProvider
import org.jetbrains.kotlin.konan.target.Family
import org.jetbrains.kotlin.konan.target.KonanTarget
import java.io.File

class NativeTargetCompilation internal constructor(
    val project: Project,
    val konanTarget: KonanTarget,
    internal val compileTask: TaskProvider<ClangCompileTask>,
    internal val archiveTask: TaskProvider<ClangArchiveTask>,
    internal val linkerTask: TaskProvider<ClangLinkerTask>,
    val sources: ConfigurableFileCollection,
    val includes: ConfigurableFileCollection,
    val linkedObjects: ConfigurableFileCollection,
    @Suppress("unused")
    val linkerArgs: ListProperty<String>,
    @Suppress("unused")
    val freeArgs: ListProperty<String>,
) : Named {
    override fun getName(): String = konanTarget.name

    fun addJniHeaders() {
        if (konanTarget.family == Family.ANDROID) {
            // android already has JNI
            return
        }

        includes.from(
            project.provider {
                val javaHome = File(System.getProperty("java.home"))
                findJniHeadersInPlayground(javaHome)
            },
        )
    }

    /**
     * Statically include the shared library output of this target with the given [dependency]'s
     * archive library output.
     */
    @Suppress("unused") // used from build.gradle
    fun include(dependency: MultiTargetNativeCompilation) {
        linkedObjects.from(dependency.sharedArchiveOutputFor(konanTarget))
    }

    /**
     * JDK ships with JNI headers only for the current platform. As a result, we don't have access
     * to cross-platform jni headers. They are mostly the same and we don't ship cross compiled code
     * from GitHub so it is acceptable to use local JNI headers for cross platform compilation on
     * GitHub.
     */
    private fun findJniHeadersInPlayground(javaHome: File): List<File> {
        val include = File(javaHome, "include")
        if (!include.exists()) {
            error("Cannot find header directory in $javaHome")
        }
        return listOf(
            include,
            File(include, "darwin"),
            File(include, "linux"),
            File(include, "win32"),
        ).filter { it.exists() }
    }
}
