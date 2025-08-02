import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.boot.gradle.plugin.SpringBootPlugin.BOM_COORDINATES
import org.springframework.boot.gradle.tasks.aot.ProcessAot
import org.springframework.boot.gradle.tasks.bundling.BootBuildImage

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.springBoot)
    alias(libs.plugins.nativeBuildTools)
    alias(libs.plugins.kotlin.jpa)
    alias(libs.plugins.openApiGenerator)
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

val dockerRepository: String by project

dependencies {
    implementation(project(":common-model"))
    implementation(platform(BOM_COORDINATES))
    implementation(libs.springBootStarter.dataJpa)
    implementation(libs.springBootStarter.validation)
    implementation(libs.springBootStarter.web)
    implementation(libs.springBootStarter.actuator)
    implementation(libs.kotlinReflect)
    implementation(libs.jacksonModuleKotlin)
    implementation(libs.flyway)
    // `implementation` is needed only to handle PSQLException in the exception handler. if that is not necessary, the dependency should be `runtimeOnly`
    implementation(libs.postgres)
    testImplementation(libs.springBootStarter.test)
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

springBoot {
    mainClass.set("com.romankudryashov.eventdrivenarchitecture.bookservice.BookServiceApplicationKt")
}

tasks.withType<KotlinCompile> {
    dependsOn(tasks.openApiGenerate)
}

tasks.withType<ProcessAot> {
    args = mutableListOf("--spring.profiles.active=test")
}

tasks.withType<BootBuildImage> {
    buildpacks = setOf("paketobuildpacks/java-native-image", "paketobuildpacks/health-checker")
    environment = mapOf("BP_HEALTH_CHECKER_ENABLED" to "true")
    imageName = "$dockerRepository:${project.name}"
}

tasks.withType<Test> {
    useJUnitPlatform()
}

val openApiPackage = "com.romankudryashov.eventdrivenarchitecture.bookservice.api"

// TODO: haven't found settings not to produce main class and build files. so it is needed to explicitly specify main class above
openApiGenerate {
    generatorName.set("kotlin-spring")
    inputSpec.set("$projectDir/src/main/resources/openapi/api.yaml")
    outputDir.set("$projectDir/build/generated/openapi")
    invokerPackage.set(openApiPackage)
    apiPackage.set("$openApiPackage.controller")
    modelPackage.set("$openApiPackage.model")

    configOptions.set(
        mapOf(
            "delegatePattern" to true.toString(),
            "documentationProvider" to "none",
            "enumPropertyNaming" to "PascalCase",
            "exceptionHandler" to false.toString(),
            "gradleBuildFile" to false.toString(),
            "useSpringBoot3" to true.toString(),
        )
    )
}

sourceSets {
    main {
        kotlin.srcDir(openApiGenerate.outputDir)
    }
}
