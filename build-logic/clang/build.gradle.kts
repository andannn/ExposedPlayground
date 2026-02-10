plugins {
    `kotlin-dsl`
}

dependencies {
    compileOnly(libs.kotlin.gradle.plugin)
    implementation(libs.android.kmp.library.gradlePlugin)
    implementation(libs.android.tool.common)
}

gradlePlugin {
    plugins {
        register("KmpLibraryPlugin") {
            id = "test.plugin2"
            implementationClass = "KmpLibraryPlugin"
        }
    }
}
