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
    //maven("https://maven.neoforged.net/releases/")
    maven {
        name = "Maven for PR #3198" // https://github.com/neoforged/NeoForge/pull/3198
        url = uri("https://prmaven.neoforged.net/NeoForge/pr3198")
        content {
            includeModule("net.neoforged", "neoforge")
            includeModule("net.neoforged", "testframework")
        }
    }
}

dependencies {
    implementation("net.fabricmc:fabric-loom:${props.getProperty("fabric_loom_version")}")
    implementation("net.neoforged:moddev-gradle:${props.getProperty("neoforge_moddev_version")}")
}
