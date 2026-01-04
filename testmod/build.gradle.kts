// Root build file for TestMod
plugins {
    java
}

val javaVersion = property("java_version").toString().toInt()

subprojects {
    apply(plugin = "java")
    
    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(javaVersion))
        }
    }
    
    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
        options.release.set(javaVersion)
    }
    
    group = property("mod_group").toString()
    version = property("mod_version").toString()
}
