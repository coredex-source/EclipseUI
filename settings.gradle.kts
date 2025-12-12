pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://maven.fabricmc.net/")
        maven("https://maven.neoforged.net/releases/")
        maven("https://maven.parchmentmc.org")
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

rootProject.name = "EclipseUI"

// EclipseCore - Core utilities
include("eclipse-core:common")
include("eclipse-core:fabric")
include("eclipse-core:neoforge")

// EclipsePlatform - Platform abstraction layer
include("eclipse-platform:common")
include("eclipse-platform:fabric")
include("eclipse-platform:neoforge")

// EclipseUI - UI library with platform-specific mods
include("eclipse-ui:common")
include("eclipse-ui:fabric")
include("eclipse-ui:neoforge")
