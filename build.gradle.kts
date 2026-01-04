// Root build file - subprojects are configured via buildSrc
plugins {
    java
    `maven-publish`
}

val binDir = layout.projectDirectory.dir("bin")

subprojects {
    apply(plugin = "java")
    
    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(BuildConfig.JAVA_VERSION))
        }
        withSourcesJar()
    }
    
    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
        options.release.set(BuildConfig.JAVA_VERSION)
    }
    
    group = BuildConfig.MOD_GROUP
    version = BuildConfig.getVersionString()
}

// Combined JAR tasks for Fabric
tasks.register<Jar>("combinedFabricJar") {
    group = "build"
    description = "Creates a combined JAR containing all EclipseUI modules for Fabric"
    
    archiveBaseName.set("EclipseUI-fabric")
    archiveVersion.set(BuildConfig.getVersionString())
    archiveClassifier.set("")
    
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    
    dependsOn(
        ":eclipse-core:common:remapJar",
        ":eclipse-core:fabric:remapJar",
        ":eclipse-platform:common:remapJar",
        ":eclipse-platform:fabric:remapJar",
        ":eclipse-ui:common:remapJar",
        ":eclipse-ui:fabric:remapJar"
    )
    
    // Use the custom combined fabric.mod.json with variable expansion
    from("combined-fabric.mod.json") {
        rename { "fabric.mod.json" }
        filter { line ->
            line.replace("@VERSION@", BuildConfig.getVersionString())
                .replace("@FABRIC_LOADER@", BuildConfig.FABRIC_LOADER_VERSION)
                .replace("@MC_MIN@", BuildConfig.MINECRAFT_VERSION)
                .replace("@MC_MAX@", BuildConfig.MINECRAFT_VERSION_MAX)
        }
    }
    
    // Include eclipseui fabric module (exclude its fabric.mod.json and nested JARs)
    from(zipTree(project(":eclipse-ui:fabric").tasks.named("remapJar").get().outputs.files.singleFile)) {
        exclude("fabric.mod.json")
        exclude("META-INF/jars/**")
    }
    
    // Include common modules directly (these contain the actual classes)
    from(zipTree(project(":eclipse-core:common").tasks.named("remapJar").get().outputs.files.singleFile)) {
        exclude("fabric.mod.json")
    }
    from(zipTree(project(":eclipse-platform:common").tasks.named("remapJar").get().outputs.files.singleFile)) {
        exclude("fabric.mod.json")
    }
    from(zipTree(project(":eclipse-ui:common").tasks.named("remapJar").get().outputs.files.singleFile)) {
        exclude("fabric.mod.json")
    }
    
    // Include fabric-specific modules (exclude their nested JARs and config files)
    from(zipTree(project(":eclipse-core:fabric").tasks.named("remapJar").get().outputs.files.singleFile)) {
        exclude("fabric.mod.json")
        exclude("META-INF/jars/**")
    }
    from(zipTree(project(":eclipse-platform:fabric").tasks.named("remapJar").get().outputs.files.singleFile)) {
        exclude("fabric.mod.json")
        exclude("META-INF/jars/**")
    }
    
    manifest {
        attributes(
            "Implementation-Title" to BuildConfig.MOD_NAME,
            "Implementation-Version" to BuildConfig.getVersionString(),
            "Implementation-Vendor" to BuildConfig.MOD_AUTHOR
        )
    }
}

tasks.register<Jar>("combinedFabricSourcesJar") {
    group = "build"
    description = "Creates a combined sources JAR for Fabric"
    
    archiveBaseName.set("EclipseUI-fabric")
    archiveVersion.set(BuildConfig.getVersionString())
    archiveClassifier.set("sources")
    
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    
    dependsOn(
        ":eclipse-core:common:remapSourcesJar",
        ":eclipse-core:fabric:remapSourcesJar",
        ":eclipse-platform:common:remapSourcesJar",
        ":eclipse-platform:fabric:remapSourcesJar",
        ":eclipse-ui:common:remapSourcesJar",
        ":eclipse-ui:fabric:remapSourcesJar"
    )
    
    from(zipTree(project(":eclipse-ui:fabric").tasks.named("remapSourcesJar").get().outputs.files.singleFile)) {
        exclude("META-INF/jars/**")
    }
    from(zipTree(project(":eclipse-core:common").tasks.named("remapSourcesJar").get().outputs.files.singleFile)) {
        exclude("fabric.mod.json")
    }
    from(zipTree(project(":eclipse-platform:common").tasks.named("remapSourcesJar").get().outputs.files.singleFile)) {
        exclude("fabric.mod.json")
    }
    from(zipTree(project(":eclipse-ui:common").tasks.named("remapSourcesJar").get().outputs.files.singleFile)) {
        exclude("fabric.mod.json")
    }
    from(zipTree(project(":eclipse-core:fabric").tasks.named("remapSourcesJar").get().outputs.files.singleFile)) {
        exclude("fabric.mod.json")
        exclude("META-INF/jars/**")
    }
    from(zipTree(project(":eclipse-platform:fabric").tasks.named("remapSourcesJar").get().outputs.files.singleFile)) {
        exclude("fabric.mod.json")
        exclude("META-INF/jars/**")
    }
}

// Combined JAR tasks for NeoForge
tasks.register<Jar>("combinedNeoForgeJar") {
    group = "build"
    description = "Creates a combined JAR containing all EclipseUI modules for NeoForge"
    
    archiveBaseName.set("EclipseUI-neoforge")
    archiveVersion.set(BuildConfig.getVersionString())
    archiveClassifier.set("")
    
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    
    dependsOn(
        ":eclipse-core:common:jar",
        ":eclipse-core:neoforge:jar",
        ":eclipse-platform:common:jar",
        ":eclipse-platform:neoforge:jar",
        ":eclipse-ui:common:jar",
        ":eclipse-ui:neoforge:jar"
    )
    
    // Include eclipseui neoforge module FIRST so its neoforge.mods.toml takes priority
    from(zipTree(project(":eclipse-ui:neoforge").tasks.named("jar").get().outputs.files.singleFile)) {
        exclude("META-INF/jarjar/**")  // Exclude nested JARs
    }
    
    // Include common modules directly
    from(zipTree(project(":eclipse-core:common").tasks.named("jar").get().outputs.files.singleFile)) {
        exclude("META-INF/neoforge.mods.toml")
    }
    from(zipTree(project(":eclipse-platform:common").tasks.named("jar").get().outputs.files.singleFile)) {
        exclude("META-INF/neoforge.mods.toml")
    }
    from(zipTree(project(":eclipse-ui:common").tasks.named("jar").get().outputs.files.singleFile)) {
        exclude("META-INF/neoforge.mods.toml")
    }
    
    // Include neoforge-specific modules
    from(zipTree(project(":eclipse-core:neoforge").tasks.named("jar").get().outputs.files.singleFile)) {
        exclude("META-INF/neoforge.mods.toml")
        exclude("META-INF/jarjar/**")
    }
    from(zipTree(project(":eclipse-platform:neoforge").tasks.named("jar").get().outputs.files.singleFile)) {
        exclude("META-INF/neoforge.mods.toml")
        exclude("META-INF/jarjar/**")
    }
    
    manifest {
        attributes(
            "Implementation-Title" to BuildConfig.MOD_NAME,
            "Implementation-Version" to BuildConfig.getVersionString(),
            "Implementation-Vendor" to BuildConfig.MOD_AUTHOR
        )
    }
}

tasks.register<Jar>("combinedNeoForgeSourcesJar") {
    group = "build"
    description = "Creates a combined sources JAR for NeoForge"
    
    archiveBaseName.set("EclipseUI-neoforge")
    archiveVersion.set(BuildConfig.getVersionString())
    archiveClassifier.set("sources")
    
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    
    dependsOn(
        ":eclipse-core:common:sourcesJar",
        ":eclipse-core:neoforge:sourcesJar",
        ":eclipse-platform:common:sourcesJar",
        ":eclipse-platform:neoforge:sourcesJar",
        ":eclipse-ui:common:sourcesJar",
        ":eclipse-ui:neoforge:sourcesJar"
    )
    
    from(zipTree(project(":eclipse-ui:neoforge").tasks.named("sourcesJar").get().outputs.files.singleFile)) {
        exclude("META-INF/jarjar/**")
    }
    from(zipTree(project(":eclipse-core:common").tasks.named("sourcesJar").get().outputs.files.singleFile)) {
        exclude("META-INF/neoforge.mods.toml")
    }
    from(zipTree(project(":eclipse-platform:common").tasks.named("sourcesJar").get().outputs.files.singleFile)) {
        exclude("META-INF/neoforge.mods.toml")
    }
    from(zipTree(project(":eclipse-ui:common").tasks.named("sourcesJar").get().outputs.files.singleFile)) {
        exclude("META-INF/neoforge.mods.toml")
    }
    from(zipTree(project(":eclipse-core:neoforge").tasks.named("sourcesJar").get().outputs.files.singleFile)) {
        exclude("META-INF/neoforge.mods.toml")
    }
    from(zipTree(project(":eclipse-platform:neoforge").tasks.named("sourcesJar").get().outputs.files.singleFile)) {
        exclude("META-INF/neoforge.mods.toml")
    }
}

// Task to copy all artifacts to bin folder
tasks.register<Copy>("copyToBin") {
    group = "build"
    description = "Copies all combined JARs to the bin folder"
    
    dependsOn("combinedFabricJar", "combinedFabricSourcesJar", "combinedNeoForgeJar", "combinedNeoForgeSourcesJar")
    
    from(tasks.named("combinedFabricJar"))
    from(tasks.named("combinedFabricSourcesJar"))
    from(tasks.named("combinedNeoForgeJar"))
    from(tasks.named("combinedNeoForgeSourcesJar"))
    
    into(binDir)
}

// Clean bin folder
tasks.register<Delete>("cleanBin") {
    group = "build"
    description = "Cleans the bin folder"
    delete(binDir)
}

tasks.named("clean") {
    dependsOn("cleanBin")
}

// Main build task that creates combined JARs and copies to bin
tasks.register("buildAll") {
    group = "build"
    description = "Builds all modules and creates combined JARs in bin folder"
    
    dependsOn("build", "copyToBin")
}

// Publishing for JitPack
publishing {
    publications {
        create<MavenPublication>("fabric") {
            groupId = "com.github.coredex-source.EclipseUI"
            artifactId = "EclipseUI-fabric"
            version = BuildConfig.getVersionString()
            
            artifact(tasks.named("combinedFabricJar"))
            artifact(tasks.named("combinedFabricSourcesJar"))
        }
        
        create<MavenPublication>("neoforge") {
            groupId = "com.github.coredex-source.EclipseUI"
            artifactId = "EclipseUI-neoforge"
            version = BuildConfig.getVersionString()
            
            artifact(tasks.named("combinedNeoForgeJar"))
            artifact(tasks.named("combinedNeoForgeSourcesJar"))
        }
    }
}
