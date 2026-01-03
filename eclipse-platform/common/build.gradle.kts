plugins {
    id("fabric-loom")
}

group = "dev.eclipseplatform"

dependencies {
    minecraft("com.mojang:minecraft:${BuildConfig.MINECRAFT_VERSION_BUILD}")
    mappings(loom.officialMojangMappings())
    
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
