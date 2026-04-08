plugins {
    id("net.fabricmc.fabric-loom")
}

repositories {
    maven("https://maven.terraformersmc.com/")
}

dependencies {
    minecraft("com.mojang:minecraft:${BuildConfig.FABRIC_MINECRAFT_VERSION_BUILD}")
    
    implementation("net.fabricmc:fabric-loader:${BuildConfig.FABRIC_LOADER_VERSION}")
    implementation("net.fabricmc.fabric-api:fabric-api:${BuildConfig.FABRIC_API_VERSION}")
    
    // ModMenu integration
    implementation("com.terraformersmc:modmenu:18.0.0-alpha.5")
    
    // Depend on EclipseCore and EclipsePlatform (separate jars in mods folder)
    // Use common modules for compilation, fabric modules provide the runtime jars
    implementation(project(":eclipse-core:common"))
    implementation(project(":eclipse-platform:common"))
    
    // Include eclipse-ui:common classes in this jar
    implementation(project(":eclipse-ui:common"))
    include(project(":eclipse-ui:common"))
}

loom {
    runs {
        named("client") {
            client()
            configName = "EclipseUI Fabric Client"
            ideConfigGenerated(true)
        }
    }
}

tasks.processResources {
    inputs.property("version", BuildConfig.getFabricVersionString())
    inputs.property("minecraft_version", BuildConfig.FABRIC_MINECRAFT_VERSION)
    inputs.property("minecraft_version_max", BuildConfig.FABRIC_MINECRAFT_VERSION_MAX)
    inputs.property("fabric_loader_version", BuildConfig.FABRIC_LOADER_VERSION)
    
    filesMatching("fabric.mod.json") {
        expand(
            "version" to BuildConfig.getFabricVersionString(),
            "minecraft_version" to BuildConfig.FABRIC_MINECRAFT_VERSION,
            "minecraft_version_max" to BuildConfig.FABRIC_MINECRAFT_VERSION_MAX,
            "fabric_loader_version" to BuildConfig.FABRIC_LOADER_VERSION
        )
    }
}

base {
    archivesName.set("EclipseUI")
}

tasks.jar {
    archiveBaseName.set("EclipseUI")
    archiveVersion.set("${BuildConfig.MOD_VERSION}-fabric-${BuildConfig.FABRIC_MINECRAFT_VERSION_BUILD}")
    archiveClassifier.set("")
}

tasks.configureEach {
    if (name == "remapJar" && this is org.gradle.jvm.tasks.Jar) {
        archiveBaseName.set("EclipseUI")
        archiveVersion.set("${BuildConfig.MOD_VERSION}-fabric-${BuildConfig.FABRIC_MINECRAFT_VERSION_BUILD}")
        archiveClassifier.set("")
    }
}

tasks.named<Jar>("sourcesJar") {
    archiveBaseName.set("EclipseUI")
    archiveVersion.set("${BuildConfig.MOD_VERSION}-fabric-${BuildConfig.FABRIC_MINECRAFT_VERSION_BUILD}")
    archiveClassifier.set("sources")
}

tasks.configureEach {
    if (name == "remapSourcesJar" && this is org.gradle.jvm.tasks.Jar) {
        archiveBaseName.set("EclipseUI")
        archiveVersion.set("${BuildConfig.MOD_VERSION}-fabric-${BuildConfig.FABRIC_MINECRAFT_VERSION_BUILD}")
        archiveClassifier.set("sources")
    }
}
