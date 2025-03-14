import org.springframework.boot.gradle.tasks.bundling.BootBuildImage

plugins {
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    // TODO: remove after https://github.com/gradle/gradle/issues/17559
    id("org.openapi.generator")
    id("org.graalvm.buildtools.native")
    kotlin("jvm")
    kotlin("plugin.spring")
    kotlin("plugin.jpa")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

val guavaVersion: String by project
val kotlinxHtmlVersion: String by project
val dockerRepository: String by project

dependencies {
    implementation(project(":common-model"))
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.flywaydb:flyway-database-postgresql")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlinx:kotlinx-html:$kotlinxHtmlVersion")
    implementation("com.google.guava:guava:$guavaVersion")
    runtimeOnly("org.postgresql:postgresql")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

tasks.withType<BootBuildImage> {
    buildpacks = setOf("paketobuildpacks/java-native-image", "paketobuildpacks/health-checker")
    environment = mapOf("BP_HEALTH_CHECKER_ENABLED" to "true")
    imageName = "$dockerRepository:${project.name}"
}

tasks.withType<Test> {
    useJUnitPlatform()
}
