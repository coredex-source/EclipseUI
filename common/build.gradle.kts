plugins {
    id("fabric-loom")
}

repositories {
    maven("https://maven.parchmentmc.org")
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
}

loom {
    // Common module doesn't need any special configuration
}

tasks.jar {
    archiveClassifier.set("common")
}
