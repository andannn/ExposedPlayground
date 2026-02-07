plugins {
    `kotlin-dsl`
}

dependencies {
}

gradlePlugin {
    plugins {
        register("TestPlugin") {
            id = "test.plugin"
            implementationClass = "TestPlugin"
        }
    }
}
