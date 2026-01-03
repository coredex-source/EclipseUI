plugins {
    id("fabric-loom")
}

group = "dev.eclipseui"

dependencies {
    minecraft("com.mojang:minecraft:${BuildConfig.MINECRAFT_VERSION_BUILD}")
    mappings(loom.officialMojangMappings())
    
    modImplementation("net.fabricmc:fabric-loader:${BuildConfig.FABRIC_LOADER_VERSION}")
    
    // Depend on EclipseCore and EclipsePlatform
    implementation(project(path = ":eclipse-core:common", configuration = "namedElements"))
    implementation(project(path = ":eclipse-platform:common", configuration = "namedElements"))
}

loom {
    // Common module - source code only, bundled into platform-specific jars
}

base {
    archivesName.set("EclipseUI-common")
}
