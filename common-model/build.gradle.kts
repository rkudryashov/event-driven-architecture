import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    id("java-library")
    id("org.springframework.boot")
    kotlin("jvm")
}

repositories {
    mavenCentral()
}

val springCoreVersion: String by project
val flywayVersion: String by project

dependencies {
    implementation("org.springframework:spring-core:$springCoreVersion")
    // TODO: remove after https://github.com/oracle/graalvm-reachability-metadata/issues/424
    implementation("org.flywaydb:flyway-database-postgresql:$flywayVersion")
}

tasks.withType<BootJar> {
    enabled = false
}
