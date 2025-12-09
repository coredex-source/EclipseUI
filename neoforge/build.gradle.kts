plugins {
    id("net.neoforged.moddev")
}

repositories {
    maven("https://maven.parchmentmc.org")
}

dependencies {
    // Include common module
    implementation(project(":common"))
}

neoForge {
    version = BuildConfig.NEOFORGE_VERSION
    
    parchment {
        if (BuildConfig.USE_PARCHMENT) {
            minecraftVersion = BuildConfig.MINECRAFT_VERSION_BUILD
            mappingsVersion = BuildConfig.PARCHMENT_VERSION
        }
    }
    
    runs {
        create("client") {
            client()
        }
    }
    
    mods {
        create(BuildConfig.MOD_ID) {
            sourceSet(sourceSets.main.get())
            sourceSet(project(":common").sourceSets.main.get())
        }
    }
}

tasks.processResources {
    inputs.property("version", BuildConfig.getVersionString())
    inputs.property("minecraft_version", BuildConfig.MINECRAFT_VERSION)
    inputs.property("minecraft_version_max", BuildConfig.MINECRAFT_VERSION_MAX)
    inputs.property("neoforge_version", BuildConfig.NEOFORGE_VERSION)
    
    filesMatching("META-INF/neoforge.mods.toml") {
        expand(
            "version" to BuildConfig.getVersionString(),
            "minecraft_version" to BuildConfig.MINECRAFT_VERSION,
            "minecraft_version_max" to BuildConfig.MINECRAFT_VERSION_MAX,
            "neoforge_version" to BuildConfig.NEOFORGE_VERSION
        )
    }
}

tasks.jar {
    from(project(":common").sourceSets.main.get().output)
    archiveBaseName.set("EclipseUI")
    archiveVersion.set("${BuildConfig.MOD_VERSION}-neoforge-${BuildConfig.MINECRAFT_VERSION_BUILD}")
    archiveClassifier.set("")
}

tasks.named<Jar>("sourcesJar") {
    archiveBaseName.set("EclipseUI")
    archiveVersion.set("${BuildConfig.MOD_VERSION}-neoforge-${BuildConfig.MINECRAFT_VERSION_BUILD}")
    archiveClassifier.set("sources")
}
