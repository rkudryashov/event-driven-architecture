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

dependencies {
    implementation("org.springframework:spring-core:$springCoreVersion")
}

tasks.withType<BootJar> {
    enabled = false
}
