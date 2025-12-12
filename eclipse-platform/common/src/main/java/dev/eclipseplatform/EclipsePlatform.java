package dev.eclipseplatform;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * EclipsePlatform - Platform abstraction layer for Eclipse libraries.
 * Provides loader detection and platform-specific utilities.
 */
public final class EclipsePlatform {
    
    public static final String MOD_ID = "eclipseplatform";
    public static final Logger LOGGER = LoggerFactory.getLogger("EclipsePlatform");
    
    private static boolean initialized = false;
    private static Platform currentPlatform = Platform.UNKNOWN;
    
    public enum Platform {
        FABRIC,
        NEOFORGE,
        UNKNOWN
    }
    
    private EclipsePlatform() {}
    
    public static void init() {
        if (initialized) return;
        initialized = true;
        
        // Detect platform
        try {
            Class.forName("net.fabricmc.loader.api.FabricLoader");
            currentPlatform = Platform.FABRIC;
        } catch (ClassNotFoundException e) {
            try {
                Class.forName("net.neoforged.fml.ModList");
                currentPlatform = Platform.NEOFORGE;
            } catch (ClassNotFoundException e2) {
                currentPlatform = Platform.UNKNOWN;
            }
        }
        
        LOGGER.info("EclipsePlatform initialized on {}", currentPlatform);
    }
    
    public static Platform getPlatform() {
        return currentPlatform;
    }
    
    public static boolean isFabric() {
        return currentPlatform == Platform.FABRIC;
    }
    
    public static boolean isNeoForge() {
        return currentPlatform == Platform.NEOFORGE;
    }
    
    public static boolean isInitialized() {
        return initialized;
    }
}
