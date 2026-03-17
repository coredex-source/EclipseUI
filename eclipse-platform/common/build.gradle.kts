plugins {
    id("net.fabricmc.fabric-loom")
}

group = "dev.eclipseplatform"

dependencies {
    minecraft("com.mojang:minecraft:${BuildConfig.MINECRAFT_VERSION_BUILD}")
    
    
    implementation("net.fabricmc:fabric-loader:${BuildConfig.FABRIC_LOADER_VERSION}")
    
    // Depend on EclipseCore
    implementation(project(":eclipse-core:common"))
}

loom {
    // Common source module
}

base {
    archivesName.set("EclipsePlatform-common")
}
