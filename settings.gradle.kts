rootProject.name = "event-driven-architecture"

include(
    "book-service",
    "user-service"
)

pluginManagement {
    val springBootVersion: String by settings
    val springDependencyManagementVersion: String by settings
    val openApiGeneratorVersion: String by settings
    val nativeBuildToolsVersion: String by settings
    val kotlinVersion: String by settings

    plugins {
        id("org.springframework.boot") version springBootVersion
        id("io.spring.dependency-management") version springDependencyManagementVersion
        id("org.openapi.generator") version openApiGeneratorVersion
        id("org.graalvm.buildtools.native") version nativeBuildToolsVersion
        kotlin("jvm") version kotlinVersion
        kotlin("plugin.spring") version kotlinVersion
        kotlin("plugin.jpa") version kotlinVersion
    }
}
