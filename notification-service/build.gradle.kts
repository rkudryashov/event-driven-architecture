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

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
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
    implementation("org.webjars:webjars-locator-lite")
    implementation("org.webjars:sockjs-client:$sockjsClientVersion")
    implementation("org.webjars:stomp-websocket:$stompWebsocketVersion")
    implementation("org.webjars:bootstrap:$bootstrapVersion")
    implementation("org.webjars:jquery:$jqueryVersion")
    runtimeOnly("org.postgresql:postgresql")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

tasks.withType<ProcessAot> {
    args = mutableListOf("--spring.profiles.active=")
}

tasks.withType<BootBuildImage> {
    buildpacks = setOf("paketobuildpacks/java-native-image", "paketobuildpacks/health-checker")
    environment = mapOf("BP_HEALTH_CHECKER_ENABLED" to "true")
    imageName = "$dockerRepository:${project.name}"
}

tasks.withType<Test> {
    useJUnitPlatform()
}
