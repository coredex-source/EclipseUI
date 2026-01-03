plugins {
    id("net.neoforged.moddev")
}

group = "dev.eclipseplatform"

neoForge {
    version = BuildConfig.NEOFORGE_VERSION
    
    mods {
        create("eclipseplatform") {
            sourceSet(sourceSets.main.get())
            sourceSet(project(":eclipse-platform:common").sourceSets.main.get())
        }
    }
}

dependencies {
    // Depend on EclipseCore (separate jar)
    compileOnly(project(":eclipse-core:common"))
    
    // Use common module sources (compiled with NeoForge mappings)
    compileOnly(project(":eclipse-platform:common"))
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
    archivesName.set("EclipsePlatform")
}

tasks.jar {
    from(project(":eclipse-platform:common").sourceSets.main.get().output)
    archiveBaseName.set("EclipsePlatform")
    archiveVersion.set("${BuildConfig.MOD_VERSION}-neoforge-${BuildConfig.MINECRAFT_VERSION_BUILD}")
    archiveClassifier.set("")
}

tasks.named<Jar>("sourcesJar") {
    from(project(":eclipse-platform:common").sourceSets.main.get().allSource)
    archiveBaseName.set("EclipsePlatform")
    archiveVersion.set("${BuildConfig.MOD_VERSION}-neoforge-${BuildConfig.MINECRAFT_VERSION_BUILD}")
    archiveClassifier.set("sources")
}
