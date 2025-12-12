package dev.eclipseui;

import dev.eclipseui.api.ConfigScreenBuilder;
import dev.eclipseui.api.Theme;
import dev.eclipseui.api.ThemeData;
import dev.eclipseui.gui.theme.ThemeRegistry;
import dev.eclipseui.impl.ConfigDataImpl;
import dev.eclipseui.impl.ConfigScreenBuilderImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main entry point for EclipseUI library.
 * 
 * <p>Example usage:</p>
 * <pre>{@code
 * Screen configScreen = EclipseUI.configScreen()
 *     .title(Component.literal("My Mod Config"))
 *     .parent(parentScreen)
 *     .theme(Theme.MODERN)
 *     .category(cat -> cat
 *         .name(Component.literal("General"))
 *         .toggle(toggle -> toggle
 *             .name(Component.literal("Enable Feature"))
 *             .binding(() -> config.enableFeature, v -> config.enableFeature = v)
 *             .defaultValue(true)
 *         )
 *         .slider(slider -> slider
 *             .name(Component.literal("Volume"))
 *             .bindingInt(() -> config.volume, v -> config.volume = v)
 *             .range(0, 100, 1)
 *             .suffix("%")
 *         )
 *     )
 *     .build();
 * }</pre>
 */
public final class EclipseUI {
    
    public static final String MOD_ID = "eclipseui";
    public static final String MOD_NAME = "EclipseUI";
    public static final String VERSION = "1.0.0";
    
    private static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);
    
    private EclipseUI() {}
    
    /**
     * Create a new config screen builder.
     */
    public static ConfigScreenBuilder configScreen() {
        return new ConfigScreenBuilderImpl();
    }
    
    /**
     * Create a config data builder for manual config management.
     */
    public static ConfigDataImpl.Builder configData() {
        return ConfigDataImpl.builder();
    }
    
    /**
     * Get the ThemeData for a built-in theme.
     */
    public static ThemeData getTheme(Theme theme) {
        return ThemeRegistry.get(theme);
    }
    
    /**
     * Create a custom theme builder starting from Modern theme defaults.
     */
    public static ThemeData.Builder customTheme() {
        return ThemeData.builder();
    }
    
    /**
     * Set a global custom theme that will be used when Theme.CUSTOM is selected.
     */
    public static void setCustomTheme(ThemeData themeData) {
        ThemeRegistry.setCustomTheme(themeData);
    }
    
    /**
     * Get the logger for EclipseUI.
     */
    public static Logger getLogger() {
        return LOGGER;
    }
    
    /**
     * Initialize the library. Called automatically by platform-specific entry points.
     */
    public static void init() {
        LOGGER.info("EclipseUI {} initialized", VERSION);
    }
}
