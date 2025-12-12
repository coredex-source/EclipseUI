plugins {
    id("fabric-loom")
}

group = "dev.eclipseplatform"

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
    
    // Depend on EclipseCore
    implementation(project(path = ":eclipse-core:common", configuration = "namedElements"))
}

loom {
    // Common source module
}

base {
    archivesName.set("EclipsePlatform-common")
}
