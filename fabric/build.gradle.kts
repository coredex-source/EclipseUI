plugins {
    id("fabric-loom")
}

repositories {
    maven("https://maven.parchmentmc.org")
    maven("https://maven.terraformersmc.com/") // ModMenu
}

dependencies {
    minecraft("com.mojang:minecraft:${BuildConfig.MINECRAFT_VERSION_BUILD}")
    
    mappings(loom.layered {
        officialMojangMappings()
        if (BuildConfig.USE_PARCHMENT) {
            parchment("org.parchmentmc.data:parchment-${BuildConfig.MINECRAFT_VERSION_BUILD}:${BuildConfig.PARCHMENT_VERSION}@zip")
        }
    })
    
    modImplementation("net.fabricmc:fabric-loader:${BuildConfig.FABRIC_LOADER_VERSION}")
    modImplementation("net.fabricmc.fabric-api:fabric-api:${BuildConfig.FABRIC_API_VERSION}")
    
    // ModMenu integration
    modImplementation("com.terraformersmc:modmenu:17.0.0-alpha.1")
    
    // Include common module
    implementation(project(":common"))
}

loom {
    runs {
        named("client") {
            client()
            configName = "Fabric Client"
            ideConfigGenerated(true)
        }
    }
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

tasks.jar {
    from(project(":common").sourceSets.main.get().output)
    archiveBaseName.set("EclipseUI")
    archiveVersion.set("${BuildConfig.MOD_VERSION}-fabric-${BuildConfig.MINECRAFT_VERSION_BUILD}")
    archiveClassifier.set("")
}

tasks.remapJar {
    archiveBaseName.set("EclipseUI")
    archiveVersion.set("${BuildConfig.MOD_VERSION}-fabric-${BuildConfig.MINECRAFT_VERSION_BUILD}")
    archiveClassifier.set("")
}

tasks.named<Jar>("sourcesJar") {
    archiveBaseName.set("EclipseUI")
    archiveVersion.set("${BuildConfig.MOD_VERSION}-fabric-${BuildConfig.MINECRAFT_VERSION_BUILD}")
    archiveClassifier.set("sources")
}

tasks.named("remapSourcesJar") {
    (this as org.gradle.jvm.tasks.Jar).archiveBaseName.set("EclipseUI")
    (this as org.gradle.jvm.tasks.Jar).archiveVersion.set("${BuildConfig.MOD_VERSION}-fabric-${BuildConfig.MINECRAFT_VERSION_BUILD}")
    (this as org.gradle.jvm.tasks.Jar).archiveClassifier.set("sources")
}
