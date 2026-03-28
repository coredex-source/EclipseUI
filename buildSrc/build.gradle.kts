import java.util.Properties
import java.io.FileInputStream

plugins {
    id("org.jetbrains.kotlin.jvm") version "2.3.20"
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
