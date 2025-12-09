// Root build file - subprojects are configured via buildSrc
plugins {
    java
}

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
