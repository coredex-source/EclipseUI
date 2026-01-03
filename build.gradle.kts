// Root build file - subprojects are configured via buildSrc
plugins {
    java
    `maven-publish`
}

val binDir = layout.projectDirectory.dir("bin")

subprojects {
    apply(plugin = "java")
    apply(plugin = "maven-publish")
    
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
    
    publishing {
        publications {
            create<MavenPublication>("mavenJava") {
                from(components["java"])
                
                pom {
                    name.set(BuildConfig.MOD_NAME)
                    description.set(BuildConfig.MOD_DESCRIPTION)
                    url.set("https://github.com/coredex-source/EclipseUI")
                    
                    licenses {
                        license {
                            name.set(BuildConfig.MOD_LICENSE)
                            url.set("https://opensource.org/licenses/MIT")
                        }
                    }
                    
                    developers {
                        developer {
                            id.set(BuildConfig.MOD_AUTHOR)
                            name.set(BuildConfig.MOD_AUTHOR)
                        }
                    }
                    
                    scm {
                        connection.set("scm:git:git://github.com/coredex-source/EclipseUI.git")
                        developerConnection.set("scm:git:ssh://github.com/coredex-source/EclipseUI.git")
                        url.set("https://github.com/coredex-source/EclipseUI")
                    }
                }
            }
        }
        
        repositories {
            maven {
                name = "GitHubPackages"
                url = uri("https://maven.pkg.github.com/coredex-source/EclipseUI")
                credentials {
                    username = System.getenv("GITHUB_ACTOR")
                    password = System.getenv("GITHUB_TOKEN")
                }
            }
        }
    }
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
        ":eclipse-core:fabric:remapJar",
        ":eclipse-platform:fabric:remapJar",
        ":eclipse-ui:fabric:remapJar"
    )
    
    from(zipTree(project(":eclipse-core:fabric").tasks.named("remapJar").get().outputs.files.singleFile))
    from(zipTree(project(":eclipse-platform:fabric").tasks.named("remapJar").get().outputs.files.singleFile))
    from(zipTree(project(":eclipse-ui:fabric").tasks.named("remapJar").get().outputs.files.singleFile))
    
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
        ":eclipse-core:fabric:remapSourcesJar",
        ":eclipse-platform:fabric:remapSourcesJar",
        ":eclipse-ui:fabric:remapSourcesJar"
    )
    
    from(zipTree(project(":eclipse-core:fabric").tasks.named("remapSourcesJar").get().outputs.files.singleFile))
    from(zipTree(project(":eclipse-platform:fabric").tasks.named("remapSourcesJar").get().outputs.files.singleFile))
    from(zipTree(project(":eclipse-ui:fabric").tasks.named("remapSourcesJar").get().outputs.files.singleFile))
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
        ":eclipse-core:neoforge:jar",
        ":eclipse-platform:neoforge:jar",
        ":eclipse-ui:neoforge:jar"
    )
    
    from(zipTree(project(":eclipse-core:neoforge").tasks.named("jar").get().outputs.files.singleFile))
    from(zipTree(project(":eclipse-platform:neoforge").tasks.named("jar").get().outputs.files.singleFile))
    from(zipTree(project(":eclipse-ui:neoforge").tasks.named("jar").get().outputs.files.singleFile))
    
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
        ":eclipse-core:neoforge:sourcesJar",
        ":eclipse-platform:neoforge:sourcesJar",
        ":eclipse-ui:neoforge:sourcesJar"
    )
    
    from(zipTree(project(":eclipse-core:neoforge").tasks.named("sourcesJar").get().outputs.files.singleFile))
    from(zipTree(project(":eclipse-platform:neoforge").tasks.named("sourcesJar").get().outputs.files.singleFile))
    from(zipTree(project(":eclipse-ui:neoforge").tasks.named("sourcesJar").get().outputs.files.singleFile))
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

// Publishing for combined JARs
publishing {
    publications {
        create<MavenPublication>("fabricCombined") {
            groupId = BuildConfig.MOD_GROUP
            artifactId = "EclipseUI-fabric"
            version = BuildConfig.getVersionString()
            
            artifact(tasks.named("combinedFabricJar"))
            artifact(tasks.named("combinedFabricSourcesJar"))
            
            pom {
                name.set("${BuildConfig.MOD_NAME} (Fabric)")
                description.set(BuildConfig.MOD_DESCRIPTION)
                url.set("https://github.com/coredex-source/EclipseUI")
                
                licenses {
                    license {
                        name.set(BuildConfig.MOD_LICENSE)
                        url.set("https://opensource.org/licenses/MIT")
                    }
                }
                
                developers {
                    developer {
                        id.set(BuildConfig.MOD_AUTHOR)
                        name.set(BuildConfig.MOD_AUTHOR)
                    }
                }
            }
        }
        
        create<MavenPublication>("neoforgeCombined") {
            groupId = BuildConfig.MOD_GROUP
            artifactId = "EclipseUI-neoforge"
            version = BuildConfig.getVersionString()
            
            artifact(tasks.named("combinedNeoForgeJar"))
            artifact(tasks.named("combinedNeoForgeSourcesJar"))
            
            pom {
                name.set("${BuildConfig.MOD_NAME} (NeoForge)")
                description.set(BuildConfig.MOD_DESCRIPTION)
                url.set("https://github.com/coredex-source/EclipseUI")
                
                licenses {
                    license {
                        name.set(BuildConfig.MOD_LICENSE)
                        url.set("https://opensource.org/licenses/MIT")
                    }
                }
                
                developers {
                    developer {
                        id.set(BuildConfig.MOD_AUTHOR)
                        name.set(BuildConfig.MOD_AUTHOR)
                    }
                }
            }
        }
    }
    
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/coredex-source/EclipseUI")
            credentials {
                username = System.getenv("GITHUB_ACTOR")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }
}
