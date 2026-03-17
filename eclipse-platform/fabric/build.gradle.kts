plugins {
    id("net.fabricmc.fabric-loom")
}

group = "dev.eclipseplatform"

dependencies {
    minecraft("com.mojang:minecraft:${BuildConfig.MINECRAFT_VERSION_BUILD}")
    
    implementation("net.fabricmc:fabric-loader:${BuildConfig.FABRIC_LOADER_VERSION}")
    
    // Depend on EclipseCore (separate jar)
    implementation(project(":eclipse-core:common"))
    
    // Include common module
    implementation(project(":eclipse-platform:common"))
    include(project(":eclipse-platform:common"))
}

loom {
    // No runs needed - this is a library
}

tasks.processResources {
    inputs.property("version", BuildConfig.getVersionString())
    inputs.property("minecraft_version", BuildConfig.MINECRAFT_VERSION)
    inputs.property("minecraft_version_max", BuildConfig.MINECRAFT_VERSION_MAX)
    inputs.property("fabric_loader_version", BuildConfig.FABRIC_LOADER_VERSION)
    
    filesMatching("fabric.mod.json") {
        expand(
            "version" to BuildConfig.getVersionString(),
            "minecraft_version" to BuildConfig.MINECRAFT_VERSION,
            "minecraft_version_max" to BuildConfig.MINECRAFT_VERSION_MAX,
            "fabric_loader_version" to BuildConfig.FABRIC_LOADER_VERSION
        )
    }
}

base {
    archivesName.set("EclipsePlatform")
}

tasks.jar {
    archiveBaseName.set("EclipsePlatform")
    archiveVersion.set("${BuildConfig.MOD_VERSION}-fabric-${BuildConfig.MINECRAFT_VERSION_BUILD}")
    archiveClassifier.set("")
}

tasks.configureEach {
    if (name == "remapJar" && this is org.gradle.jvm.tasks.Jar) {
        archiveBaseName.set("EclipsePlatform")
        archiveVersion.set("${BuildConfig.MOD_VERSION}-fabric-${BuildConfig.MINECRAFT_VERSION_BUILD}")
        archiveClassifier.set("")
    }
}

tasks.named<Jar>("sourcesJar") {
    archiveBaseName.set("EclipsePlatform")
    archiveVersion.set("${BuildConfig.MOD_VERSION}-fabric-${BuildConfig.MINECRAFT_VERSION_BUILD}")
    archiveClassifier.set("sources")
}

tasks.configureEach {
    if (name == "remapSourcesJar" && this is org.gradle.jvm.tasks.Jar) {
        archiveBaseName.set("EclipsePlatform")
        archiveVersion.set("${BuildConfig.MOD_VERSION}-fabric-${BuildConfig.MINECRAFT_VERSION_BUILD}")
        archiveClassifier.set("sources")
    }
}
