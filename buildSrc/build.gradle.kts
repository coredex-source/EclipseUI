import java.util.Properties
import java.io.FileInputStream

plugins {
    `kotlin-dsl`
}

val props = Properties().apply {
    FileInputStream(rootDir.parentFile.resolve("gradle.properties")).use { load(it) }
}

repositories {
    gradlePluginPortal()
    maven("https://maven.fabricmc.net/")
    maven("https://maven.neoforged.net/releases/")
}

dependencies {
    implementation("net.fabricmc:fabric-loom:${props.getProperty("fabric_loom_version")}")
    implementation("net.neoforged:moddev-gradle:${props.getProperty("neoforge_moddev_version")}")
}
