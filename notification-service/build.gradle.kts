import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.springframework.boot.gradle.tasks.aot.ProcessAot
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

repositories {
    mavenCentral()
}

val sockjsClientVersion: String by project
val stompWebsocketVersion: String by project
val bootstrapVersion: String by project
val jqueryVersion: String by project
val dockerRepository: String by project

dependencies {
    implementation(project(":common-model"))
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-mail")
    implementation("org.springframework.boot:spring-boot-starter-websocket")
    implementation("org.flywaydb:flyway-database-postgresql")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    // "webjars" dependencies are needed to serve `index.html` and its resources
    // TODO: add `webjars-locator-lite` dependency and do not use specific versions of assets after https://github.com/spring-projects/spring-boot/issues/40146 will be resolved
    implementation("org.webjars:sockjs-client:$sockjsClientVersion")
    implementation("org.webjars:stomp-websocket:$stompWebsocketVersion")
    implementation("org.webjars:bootstrap:$bootstrapVersion")
    implementation("org.webjars:jquery:$jqueryVersion")
    runtimeOnly("org.postgresql:postgresql")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_21)
        freeCompilerArgs.add("-Xjsr305=strict")
    }
}

tasks.withType<ProcessAot> {
    args = mutableListOf("--spring.profiles.active=")
}

tasks.withType<BootBuildImage> {
    buildpacks = setOf("gcr.io/paketo-buildpacks/java-native-image", "gcr.io/paketo-buildpacks/health-checker")
    environment = mapOf(
        "BP_HEALTH_CHECKER_ENABLED" to "true",
        "THC_PATH" to "/actuator/health"
    )
    imageName = "$dockerRepository:${project.name}"
}

tasks.withType<Test> {
    useJUnitPlatform()
}