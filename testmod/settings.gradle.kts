pluginManagement {
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
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

rootProject.name = "EclipseUI-TestMod"

include("fabric")
include("neoforge")
