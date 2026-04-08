import java.util.Properties
import java.io.FileInputStream

object BuildConfig {
    private val props: Properties by lazy {
        Properties().apply {
            FileInputStream("gradle.properties").use { load(it) }
        }
    }

    private fun property(name: String): String {
        return props.getProperty(name)
            ?: error("Missing required property '$name' in gradle.properties")
    }

    private fun property(name: String, fallbackName: String): String {
        return props.getProperty(name)
            ?: props.getProperty(fallbackName)
            ?: error("Missing required property '$name' (or fallback '$fallbackName') in gradle.properties")
    }
    
    // Mod info
    val MOD_ID: String
        get() = property("mod_id")
    val MOD_NAME: String
        get() = property("mod_name")
    val MOD_VERSION: String
        get() = property("mod_version")
    val MOD_GROUP: String
        get() = property("mod_group")
    val MOD_DESCRIPTION: String
        get() = property("mod_description")
    val MOD_LICENSE: String
        get() = property("mod_license")
    val MOD_AUTHOR: String
        get() = property("mod_author")
    
    // Minecraft
    val MINECRAFT_VERSION: String
        get() = property("minecraft_version")
    val MINECRAFT_VERSION_MAX: String
        get() = property("minecraft_version_max")
    val MINECRAFT_VERSION_BUILD: String
        get() = property("minecraft_version_build")

    // Fabric-specific Minecraft (falls back to shared minecraft_* keys)
    val FABRIC_MINECRAFT_VERSION: String
        get() = property("fabric_minecraft_version", "minecraft_version")
    val FABRIC_MINECRAFT_VERSION_MAX: String
        get() = property("fabric_minecraft_version_max", "minecraft_version_max")
    val FABRIC_MINECRAFT_VERSION_BUILD: String
        get() = property("fabric_minecraft_version_build", "minecraft_version_build")

    // NeoForge-specific Minecraft (falls back to shared minecraft_* keys)
    val NEOFORGE_MINECRAFT_VERSION: String
        get() = property("neoforge_minecraft_version", "minecraft_version")
    val NEOFORGE_MINECRAFT_VERSION_MAX: String
        get() = property("neoforge_minecraft_version_max", "minecraft_version_max")
    val NEOFORGE_MINECRAFT_VERSION_BUILD: String
        get() = property("neoforge_minecraft_version_build", "minecraft_version_build")

    val JAVA_VERSION: Int
        get() = property("java_version").toInt()
    
    // Fabric
    val FABRIC_LOADER_VERSION: String
        get() = property("fabric_loader_version")
    val FABRIC_API_VERSION: String
        get() = property("fabric_api_version")
    val FABRIC_LOOM_VERSION: String
        get() = property("fabric_loom_version")
    
    // NeoForge
    val NEOFORGE_VERSION: String
        get() = property("neoforge_version")
    val NEOFORGE_MODDEV_VERSION: String
        get() = property("neoforge_moddev_version")
    
    // Build version string
    fun getVersionString(): String {
        return "$MOD_VERSION+mc$MINECRAFT_VERSION_BUILD"
    }

    fun getFabricVersionString(): String {
        return "$MOD_VERSION+mc$FABRIC_MINECRAFT_VERSION_BUILD"
    }

    fun getNeoForgeVersionString(): String {
        return "$MOD_VERSION+mc$NEOFORGE_MINECRAFT_VERSION_BUILD"
    }
}
