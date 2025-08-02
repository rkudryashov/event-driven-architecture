import org.springframework.boot.gradle.plugin.SpringBootPlugin.BOM_COORDINATES
import org.springframework.boot.gradle.tasks.bundling.BootBuildImage

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.springBoot)
    alias(libs.plugins.nativeBuildTools)
    alias(libs.plugins.kotlin.jpa)
    // TODO: remove after https://github.com/gradle/gradle/issues/17559
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
    implementation(libs.springBootStarter.web)
    implementation(libs.springBootStarter.actuator)
    implementation(libs.kotlinReflect)
    implementation(libs.jacksonModuleKotlin)
    implementation(libs.flyway)
    implementation(libs.kotlinxHtml)
    implementation(libs.guava)
    runtimeOnly(libs.postgres)
    testImplementation(libs.springBootStarter.test)
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
