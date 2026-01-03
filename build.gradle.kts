// Root build file - subprojects are configured via buildSrc
plugins {
    java
    `maven-publish`
}

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
