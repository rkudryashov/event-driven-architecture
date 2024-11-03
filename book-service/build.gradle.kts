import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.boot.gradle.tasks.aot.ProcessAot
import org.springframework.boot.gradle.tasks.bundling.BootBuildImage

plugins {
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    id("org.openapi.generator")
    id("org.graalvm.buildtools.native")
    kotlin("jvm")
    kotlin("plugin.spring")
    kotlin("plugin.jpa")
}

repositories {
    mavenCentral()
}

val dockerRepository: String by project

dependencies {
    implementation(project(":common-model"))
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.flywaydb:flyway-database-postgresql")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    // `implementation` is needed only to handle PSQLException in the exception handler. if that is not necessary, the dependency should be `runtimeOnly`
    implementation("org.postgresql:postgresql")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_21)
        freeCompilerArgs.add("-Xjsr305=strict")
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
    buildpacks = setOf("gcr.io/paketo-buildpacks/java-native-image", "gcr.io/paketo-buildpacks/health-checker")
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
