plugins {
    id("net.fabricmc.fabric-loom")
}

group = "dev.eclipsecore"

dependencies {
    minecraft("com.mojang:minecraft:${BuildConfig.MINECRAFT_VERSION_BUILD}")
    
    implementation("net.fabricmc:fabric-loader:${BuildConfig.FABRIC_LOADER_VERSION}")
}

loom {
    // Common source module
}

base {
    archivesName.set("EclipseCore-common")
}
