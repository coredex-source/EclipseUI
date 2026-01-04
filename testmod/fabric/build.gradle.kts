plugins {
    id("fabric-loom") version "1.14-SNAPSHOT"
}

repositories {
    mavenCentral()
    maven("https://maven.fabricmc.net/")
    maven("https://maven.terraformersmc.com/")
    maven("https://jitpack.io")
}

dependencies {
    minecraft("com.mojang:minecraft:${property("minecraft_version").toString()}")
    mappings(loom.officialMojangMappings())
    
    modImplementation("net.fabricmc:fabric-loader:${property("fabric_loader_version").toString()}")
    modImplementation("net.fabricmc.fabric-api:fabric-api:${property("fabric_api_version").toString()}")
    
    // ModMenu integration
    modImplementation("com.terraformersmc:modmenu:17.0.0-alpha.1")
    
    // EclipseUI from JitPack
    modImplementation("com.github.coredex-source.EclipseUI:EclipseUI-fabric:${property("eclipseui_version").toString()}")
}

loom {
    runs {
        named("client") {
            client()
            configName = "TestMod Fabric Client"
            ideConfigGenerated(true)
        }
    }
}

tasks.processResources {
    val modVersion = project.property("mod_version").toString()
    val minecraftVersion = project.property("minecraft_version").toString()
    val fabricLoaderVersion = project.property("fabric_loader_version").toString()
    
    inputs.property("version", modVersion)
    inputs.property("minecraft_version", minecraftVersion)
    inputs.property("fabric_loader_version", fabricLoaderVersion)
    
    filesMatching("fabric.mod.json") {
        expand(
            "version" to modVersion,
            "minecraft_version" to minecraftVersion,
            "fabric_loader_version" to fabricLoaderVersion
        )
    }
}

base {
    archivesName.set("EclipseUI-TestMod-fabric")
}
