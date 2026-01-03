plugins {
    id("fabric-loom")
}

group = "dev.eclipsecore"

dependencies {
    minecraft("com.mojang:minecraft:${BuildConfig.MINECRAFT_VERSION_BUILD}")
    mappings(loom.officialMojangMappings())
    
    modImplementation("net.fabricmc:fabric-loader:${BuildConfig.FABRIC_LOADER_VERSION}")
    
    // Include common module
    implementation(project(path = ":eclipse-core:common", configuration = "namedElements"))
    include(project(":eclipse-core:common"))
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
    archivesName.set("EclipseCore")
}

tasks.jar {
    archiveBaseName.set("EclipseCore")
    archiveVersion.set("${BuildConfig.MOD_VERSION}-fabric-${BuildConfig.MINECRAFT_VERSION_BUILD}")
    archiveClassifier.set("")
}

tasks.remapJar {
    archiveBaseName.set("EclipseCore")
    archiveVersion.set("${BuildConfig.MOD_VERSION}-fabric-${BuildConfig.MINECRAFT_VERSION_BUILD}")
    archiveClassifier.set("")
}

tasks.named<Jar>("sourcesJar") {
    archiveBaseName.set("EclipseCore")
    archiveVersion.set("${BuildConfig.MOD_VERSION}-fabric-${BuildConfig.MINECRAFT_VERSION_BUILD}")
    archiveClassifier.set("sources")
}

tasks.named("remapSourcesJar") {
    (this as org.gradle.jvm.tasks.Jar).archiveBaseName.set("EclipseCore")
    (this as org.gradle.jvm.tasks.Jar).archiveVersion.set("${BuildConfig.MOD_VERSION}-fabric-${BuildConfig.MINECRAFT_VERSION_BUILD}")
    (this as org.gradle.jvm.tasks.Jar).archiveClassifier.set("sources")
}
