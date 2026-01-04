plugins {
    id("net.neoforged.moddev") version "2.0.137"
}

repositories {
    mavenCentral()
    maven("https://maven.neoforged.net/releases/")
    maven("https://jitpack.io")
}

dependencies {
    // EclipseUI from JitPack
    implementation("com.github.coredex-source.EclipseUI:EclipseUI-neoforge:${property("eclipseui_version").toString()}")
}

neoForge {
    version = property("neoforge_version").toString()
    
    runs {
        create("client") {
            client()
        }
    }
    
    mods {
        create("eclipseui_testmod") {
            sourceSet(sourceSets.main.get())
        }
    }
}

tasks.processResources {
    val modVersion = project.property("mod_version").toString()
    val minecraftVersion = project.property("minecraft_version").toString()
    val neoforgeVersion = project.property("neoforge_version").toString()
    
    inputs.property("version", modVersion)
    inputs.property("minecraft_version", minecraftVersion)
    inputs.property("neoforge_version", neoforgeVersion)
    
    filesMatching("META-INF/neoforge.mods.toml") {
        expand(
            "version" to modVersion,
            "minecraft_version" to minecraftVersion,
            "neoforge_version" to neoforgeVersion
        )
    }
}

base {
    archivesName.set("EclipseUI-TestMod-neoforge")
}
