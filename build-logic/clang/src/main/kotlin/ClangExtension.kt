import org.gradle.api.Action
import org.gradle.api.NamedDomainObjectCollection
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.plugin.KotlinMultiplatformPluginWrapper
import org.jetbrains.kotlin.gradle.plugin.KotlinTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeCompilation
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTargetWithHostTests
import org.jetbrains.kotlin.konan.target.LinkerOutputKind

abstract class ClangExtension(
    val project: Project,
) {
    private val clang = AndroidXClang(project)
    private val kotlinExtensionDelegate =
        lazy {
            project.afterEvaluate {
                // Workaround for KT-77732
                project.tasks
                    .named { it == "commonizeNativeDistribution" }
                    .configureEach { dependsOn("downloadKotlinNativeDistribution") }
                project.tasks
                    .named { it == "downloadKotlinNativeDistribution" }
                    .configureEach { outputs.cacheIf { false } }
            }
            project.plugins.apply(KotlinMultiplatformPluginWrapper::class.java)
            project.extensions.findByType(KotlinMultiplatformExtension::class.java)
                ?: error("KotlinMultiplatformExtension not found")
        }
    private val kotlinExtension: KotlinMultiplatformExtension by kotlinExtensionDelegate

    val targets: NamedDomainObjectCollection<KotlinTarget>
        get() = kotlinExtension.targets

    /**
     * Creates a multi-target native compilation with the given [archiveName].
     *
     * The given [configure] action can be used to add targets, sources, includes etc.
     *
     * The outputs of this compilation is not added to any artifact by default.
     * * To use the outputs via cinterop (kotlin native), use the [createCinterop] function.
     * * To bundle the outputs inside a JAR (to be loaded at runtime), use the
     *   [addNativeLibrariesToResources] function.
     * * To bundle the outputs inside an AAR (to be loaded at runtime), use the
     *   [addNativeLibrariesToJniLibs] function.
     *
     * @param archiveName The archive file name for the native artifacts (.so, .a or .o)
     * @param outputKind The kind of output it should be produced (library or executable).
     * @param configure Action block to configure the compilation.
     */
    @JvmOverloads
    fun createNativeCompilation(
        archiveName: String,
        outputKind: LinkerOutputKind = LinkerOutputKind.DYNAMIC_LIBRARY,
        configure: Action<MultiTargetNativeCompilation>,
    ): MultiTargetNativeCompilation =
        clang.createNativeCompilation(
            archiveName = archiveName,
            configure = configure,
            outputKind = outputKind,
        )

    /**
     * Creates a Kotlin Native cinterop configuration for the given [nativeTarget] main compilation
     * from the outputs of [nativeCompilation].
     *
     * @param nativeTarget The kotlin native target for which a new cinterop will be added on the
     *   main compilation.
     * @param nativeCompilation The [MultiTargetNativeCompilation] which will be embedded into the
     *   generated cinterop klib.
     * @param cinteropName The name of the cinterop definition. A matching "<cinteropName.def>" file
     *   needs to be present in the default cinterop location
     *   (src/nativeInterop/cinterop/<cinteropName.def>).
     */
    @JvmOverloads
    fun createCinterop(
        nativeTarget: KotlinNativeTarget,
        nativeCompilation: MultiTargetNativeCompilation,
        cinteropName: String = nativeCompilation.archiveName,
    ) {
        createCinterop(
            kotlinNativeCompilation =
                nativeTarget.compilations.getByName(KotlinCompilation.MAIN_COMPILATION_NAME)
                    as KotlinNativeCompilation,
            nativeCompilation = nativeCompilation,
            cinteropName = cinteropName,
        )
    }

    @JvmOverloads
    fun createCinterop(
        kotlinNativeCompilation: KotlinNativeCompilation,
        nativeCompilation: MultiTargetNativeCompilation,
        cinteropName: String = nativeCompilation.archiveName,
    ) {
        nativeCompilation.configureCinterop(
            kotlinNativeCompilation = kotlinNativeCompilation,
            cinteropName = cinteropName,
        )
    }

    @JvmOverloads
    fun androidNativeX86(block: Action<KotlinNativeTarget>? = null): KotlinNativeTarget? =
        if (project.enableAndroidNative()) {
            kotlinExtension.androidNativeX86 { block?.execute(this) }
        } else {
            null
        }

    @JvmOverloads
    fun androidNativeX64(block: Action<KotlinNativeTarget>? = null): KotlinNativeTarget? =
        if (project.enableAndroidNative()) {
            kotlinExtension.androidNativeX64 { block?.execute(this) }
        } else {
            null
        }

    @JvmOverloads
    fun androidNativeArm64(block: Action<KotlinNativeTarget>? = null): KotlinNativeTarget? =
        if (project.enableAndroidNative()) {
            kotlinExtension.androidNativeArm64 { block?.execute(this) }
        } else {
            null
        }

    @JvmOverloads
    fun androidNativeArm32(block: Action<KotlinNativeTarget>? = null): KotlinNativeTarget? =
        if (project.enableAndroidNative()) {
            kotlinExtension.androidNativeArm32 { block?.execute(this) }
        } else {
            null
        }

    @JvmOverloads
    fun macosArm64(block: Action<KotlinNativeTarget>? = null): KotlinNativeTargetWithHostTests? =
        if (project.enableMac()) {
            kotlinExtension.macosArm64 { block?.execute(this) }
        } else {
            null
        }

    /** Configures all ios targets supported by AndroidX. */
    @JvmOverloads
    fun ios(block: Action<KotlinNativeTarget>? = null): List<KotlinNativeTarget> = listOfNotNull(iosArm64(block), iosSimulatorArm64(block))

    @JvmOverloads
    fun iosArm64(block: Action<KotlinNativeTarget>? = null): KotlinNativeTarget? =
        if (project.enableMac()) {
            kotlinExtension.iosArm64 { block?.execute(this) }
        } else {
            null
        }

    @JvmOverloads
    fun iosSimulatorArm64(block: Action<KotlinNativeTarget>? = null): KotlinNativeTarget? =
        if (project.enableMac()) {
            kotlinExtension.iosSimulatorArm64 { block?.execute(this) }
        } else {
            null
        }

    @JvmOverloads
    fun linuxArm64(block: Action<KotlinNativeTarget>? = null): KotlinNativeTarget? =
        if (project.enableLinux()) {
            kotlinExtension.linuxArm64 { block?.execute(this) }
        } else {
            null
        }

    companion object {
        const val EXTENSION_NAME = "clang"
    }
}

fun Project.enableJs(): Boolean = true

fun Project.enableAndroidNative(): Boolean = true

fun Project.enableMac(): Boolean = true

fun Project.enableWindows(): Boolean = true

fun Project.enableLinux(): Boolean = true

fun Project.enableJvm(): Boolean = true

fun Project.enableDesktop(): Boolean = true

fun Project.enableWasmJs(): Boolean = true
