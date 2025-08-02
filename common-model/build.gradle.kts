import org.springframework.boot.gradle.plugin.SpringBootPlugin.BOM_COORDINATES
import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    id("java-library")
    alias(libs.plugins.springBoot)
    alias(libs.plugins.kotlin.jvm)
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(platform(BOM_COORDINATES))
    implementation(libs.springCore)
}

tasks.withType<BootJar> {
    enabled = false
}
