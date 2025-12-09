import java.util.Properties
import java.io.FileInputStream

object BuildConfig {
    private val props = Properties().apply {
        FileInputStream("gradle.properties").use { load(it) }
    }
    
    // Mod info
    val MOD_ID: String = props.getProperty("mod_id")
    val MOD_NAME: String = props.getProperty("mod_name")
    val MOD_VERSION: String = props.getProperty("mod_version")
    val MOD_GROUP: String = props.getProperty("mod_group")
    val MOD_DESCRIPTION: String = props.getProperty("mod_description")
    val MOD_LICENSE: String = props.getProperty("mod_license")
    val MOD_AUTHOR: String = props.getProperty("mod_author")
    
    // Minecraft
    val MINECRAFT_VERSION: String = props.getProperty("minecraft_version")
    val MINECRAFT_VERSION_MAX: String = props.getProperty("minecraft_version_max")
    val MINECRAFT_VERSION_BUILD: String = props.getProperty("minecraft_version_build")
    val JAVA_VERSION: Int = props.getProperty("java_version").toInt()
    
    // Fabric
    val FABRIC_LOADER_VERSION: String = props.getProperty("fabric_loader_version")
    val FABRIC_API_VERSION: String = props.getProperty("fabric_api_version")
    val FABRIC_LOOM_VERSION: String = props.getProperty("fabric_loom_version")
    
    // NeoForge
    val NEOFORGE_VERSION: String = props.getProperty("neoforge_version")
    val NEOFORGE_MODDEV_VERSION: String = props.getProperty("neoforge_moddev_version")
    
    // Parchment mappings
    val PARCHMENT_VERSION: String = props.getProperty("parchment_version")
    val USE_PARCHMENT: Boolean = props.getProperty("use_parchment").toBoolean()
    
    // Build version string
    fun getVersionString(): String {
        return "$MOD_VERSION+mc$MINECRAFT_VERSION_BUILD"
    }
}
