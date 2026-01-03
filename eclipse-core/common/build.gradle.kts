plugins {
    id("fabric-loom")
}

group = "dev.eclipsecore"

dependencies {
    minecraft("com.mojang:minecraft:${BuildConfig.MINECRAFT_VERSION_BUILD}")
    mappings(loom.officialMojangMappings())
    
    modImplementation("net.fabricmc:fabric-loader:${BuildConfig.FABRIC_LOADER_VERSION}")
}

loom {
    // Common source module
}

base {
    archivesName.set("EclipseCore-common")
}
