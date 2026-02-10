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
        register("TestPlugin2") {
            id = "test.plugin2"
            implementationClass = "TestPlugin2"
        }
    }
}
