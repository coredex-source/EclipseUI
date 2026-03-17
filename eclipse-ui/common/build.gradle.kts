plugins {
    id("net.fabricmc.fabric-loom")
}

group = "dev.eclipseui"

dependencies {
    minecraft("com.mojang:minecraft:${BuildConfig.MINECRAFT_VERSION_BUILD}")
    
    
    implementation("net.fabricmc:fabric-loader:${BuildConfig.FABRIC_LOADER_VERSION}")
    
    // Depend on EclipseCore and EclipsePlatform
    implementation(project(":eclipse-core:common"))
    implementation(project(":eclipse-platform:common"))
}

loom {
    // Common module - source code only, bundled into platform-specific jars
}

base {
    archivesName.set("EclipseUI-common")
}
