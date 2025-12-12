plugins {
    id("net.neoforged.moddev")
}

repositories {
    maven("https://maven.parchmentmc.org")
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
            sourceSet(project(":eclipse-ui:common").sourceSets.main.get())
        }
    }
}

dependencies {
    // Depend on EclipseCore and EclipsePlatform (separate jars in mods folder)
    compileOnly(project(":eclipse-core:common"))
    compileOnly(project(":eclipse-platform:common"))
    
    // Use common module sources (compiled with NeoForge mappings)
    compileOnly(project(":eclipse-ui:common"))
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

base {
    archivesName.set("EclipseUI")
}

tasks.jar {
    from(project(":eclipse-ui:common").sourceSets.main.get().output)
    archiveBaseName.set("EclipseUI")
    archiveVersion.set("${BuildConfig.MOD_VERSION}-neoforge-${BuildConfig.MINECRAFT_VERSION_BUILD}")
    archiveClassifier.set("")
}

tasks.named<Jar>("sourcesJar") {
    from(project(":eclipse-ui:common").sourceSets.main.get().allSource)
    archiveBaseName.set("EclipseUI")
    archiveVersion.set("${BuildConfig.MOD_VERSION}-neoforge-${BuildConfig.MINECRAFT_VERSION_BUILD}")
    archiveClassifier.set("sources")
}
