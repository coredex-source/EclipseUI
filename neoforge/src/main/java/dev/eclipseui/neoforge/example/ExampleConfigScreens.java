package dev.eclipseui.neoforge.example;

import dev.eclipseui.EclipseUI;
import dev.eclipseui.api.Theme;
import dev.eclipseui.neoforge.example.ExampleConfig.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/**
 * Factory class for creating example config screens demonstrating all themes.
 * 
 * Each method creates a complete config screen with the specified theme,
 * showing various widget types and organization patterns.
 */
public class ExampleConfigScreens {
    
    /**
     * Helper method to get a human-readable theme name.
     */
    private static String getThemeName(Theme theme) {
        return switch(theme) {
            case FAITHFUL -> "Faithful Theme";
            case MODERN -> "Modern Theme";
            case CUSTOM -> "Custom Theme";
        };
    }
    
    /**
     * Creates a config screen with the FAITHFUL theme.
     * This theme uses vanilla Minecraft-style buttons and controls.
     */
    public static Screen createFaithfulScreen(Screen parent) {
        ExampleConfig config = ExampleConfig.get();
        
        return EclipseUI.configScreen()
            .title(Component.literal("Example Mod - Faithful Theme"))
            .parent(parent)
            .theme(Theme.FAITHFUL)
            .onSave(config::save)
            .onReset(config::resetToDefaults)
            
            // General Category
            .category(cat -> cat
                .name(Component.literal("General"))
                .description(Component.literal("Basic mod settings"))
                
                .toggle(t -> t
                    .name(Component.literal("Enable Mod"))
                    .description(Component.literal("Master toggle for all mod features"))
                    .binding(() -> config.enableMod, v -> config.enableMod = v)
                    .defaultValue(true)
                )
                
                .toggle(t -> t
                    .name(Component.literal("Show Notifications"))
                    .description(Component.literal("Display popup notifications for events"))
                    .binding(() -> config.showNotifications, v -> config.showNotifications = v)
                    .defaultValue(true)
                )
                
                .separator()
                
                .toggle(t -> t
                    .name(Component.literal("Debug Mode"))
                    .description(Component.literal("Enable debug logging and overlays"))
                    .binding(() -> config.debugMode, v -> config.debugMode = v)
                    .defaultValue(false)
                )
                
                .toggle(t -> t
                    .name(Component.literal("Experimental Features"))
                    .description(Component.literal("Enable unstable experimental features\n§cMay cause issues!"))
                    .binding(() -> config.experimentalFeatures, v -> config.experimentalFeatures = v)
                    .defaultValue(false)
                    .requiresRestart(true)
                )
            )
            
            // Graphics Category
            .category(cat -> cat
                .name(Component.literal("Graphics"))
                .description(Component.literal("Visual settings and rendering options"))
                
                .label(Component.literal("§lQuality Settings"))
                
                .<GraphicsQuality>dropdown(d -> d
                    .name(Component.literal("Graphics Quality"))
                    .description(Component.literal("Overall graphics quality preset"))
                    .enumClass(GraphicsQuality.class)
                    .binding(() -> config.graphicsQuality, v -> config.graphicsQuality = v)
                    .defaultValue(GraphicsQuality.MEDIUM)
                )
                
                .slider(s -> s
                    .name(Component.literal("Render Distance"))
                    .description(Component.literal("How far to render the world (in chunks)"))
                    .range(2, 32, 1)
                    .bindingInt(() -> config.renderDistance, v -> config.renderDistance = v)
                    .defaultValue(12)
                    .suffix(" chunks")
                )
                
                .slider(s -> s
                    .name(Component.literal("Brightness"))
                    .description(Component.literal("Screen brightness level"))
                    .range(0.0, 2.0, 0.1)
                    .bindingDouble(() -> config.brightness, v -> config.brightness = v)
                    .defaultValue(1.0)
                    .percentageFormat()
                )
                
                .separator()
                .label(Component.literal("§lEffects"))
                
                .toggle(t -> t
                    .name(Component.literal("Enable Shaders"))
                    .description(Component.literal("Use shader-based rendering effects"))
                    .binding(() -> config.enableShaders, v -> config.enableShaders = v)
                    .defaultValue(false)
                )
                
                .toggle(t -> t
                    .name(Component.literal("Fancy Particles"))
                    .description(Component.literal("Use enhanced particle effects"))
                    .binding(() -> config.fancyParticles, v -> config.fancyParticles = v)
                    .defaultValue(true)
                )
                
                .slider(s -> s
                    .name(Component.literal("Max FPS"))
                    .description(Component.literal("Frame rate limit (0 = unlimited)"))
                    .range(0, 240, 10)
                    .bindingInt(() -> config.maxFps, v -> config.maxFps = v)
                    .defaultValue(60)
                    .suffix(" FPS")
                )
            )
            
            // Audio Category  
            .category(cat -> cat
                .name(Component.literal("Audio"))
                .description(Component.literal("Sound and music settings"))
                
                .slider(s -> s
                    .name(Component.literal("Master Volume"))
                    .description(Component.literal("Overall volume level"))
                    .range(0, 100, 5)
                    .bindingInt(() -> config.masterVolume, v -> config.masterVolume = v)
                    .defaultValue(100)
                    .suffix("%")
                )
                
                .slider(s -> s
                    .name(Component.literal("Music Volume"))
                    .description(Component.literal("Background music volume"))
                    .range(0, 100, 5)
                    .bindingInt(() -> config.musicVolume, v -> config.musicVolume = v)
                    .defaultValue(80)
                    .suffix("%")
                )
                
                .slider(s -> s
                    .name(Component.literal("Sound Effects"))
                    .description(Component.literal("Sound effects volume"))
                    .range(0, 100, 5)
                    .bindingInt(() -> config.sfxVolume, v -> config.sfxVolume = v)
                    .defaultValue(100)
                    .suffix("%")
                )
                
                .separator()
                
                .<SoundMode>dropdown(d -> d
                    .name(Component.literal("Sound Mode"))
                    .description(Component.literal("Audio output configuration"))
                    .enumClass(SoundMode.class)
                    .binding(() -> config.soundMode, v -> config.soundMode = v)
                    .defaultValue(SoundMode.STEREO)
                )
                
                .toggle(t -> t
                    .name(Component.literal("Mute When Unfocused"))
                    .description(Component.literal("Mute audio when game loses focus"))
                    .binding(() -> config.muteWhenUnfocused, v -> config.muteWhenUnfocused = v)
                    .defaultValue(false)
                )
            )
            
            .build();
    }
    
    /**
     * Creates a config screen with the MODERN theme.
     * This theme features a clean, flat design with smooth animations.
     */
    public static Screen createModernScreen(Screen parent) {
        ExampleConfig config = ExampleConfig.get();
        
        return EclipseUI.configScreen()
            .title(Component.literal("Example Mod - " + getThemeName(config.theme)))
            .parent(parent)
            .theme(config.theme)
            .onSave(config::save)
            .onReset(config::resetToDefaults)
            
            // Appearance Category - showcasing color pickers
            .category(cat -> cat
                .name(Component.literal("Appearance"))
                .description(Component.literal("Customize the visual appearance"))
                
                .label(Component.literal("§lTheme"))
                
                .<Theme>dropdown(d -> d
                    .name(Component.literal("UI Theme"))
                    .description(Component.literal("Select the visual theme for config screens\n§7Changes apply immediately"))
                    .enumClass(Theme.class)
                    .binding(() -> config.theme, v -> config.theme = v)
                    .defaultValue(Theme.MODERN)
                    .formatter(theme -> Component.literal(switch(theme) {
                        case FAITHFUL -> "Faithful";
                        case MODERN -> "Modern";
                        case CUSTOM -> "Custom";
                    }))
                    .onChange(newTheme -> {
                        // Reopen screen with new theme
                        Minecraft mc = Minecraft.getInstance();
                        mc.setScreen(createModernScreen(parent));
                    })
                )
                
                .separator()
                .label(Component.literal("§lColor Scheme"))
                
                .colorPicker(c -> c
                    .name(Component.literal("Primary Color"))
                    .description(Component.literal("Main theme color used throughout the UI"))
                    .binding(() -> config.primaryColor, v -> config.primaryColor = v)
                    .defaultValue(0xFF5555FF)
                    .showAlpha(false)
                )
                
                .colorPicker(c -> c
                    .name(Component.literal("Secondary Color"))
                    .description(Component.literal("Accent color for highlights"))
                    .binding(() -> config.secondaryColor, v -> config.secondaryColor = v)
                    .defaultValue(0xFF55FF55)
                    .showAlpha(false)
                )
                
                .colorPicker(c -> c
                    .name(Component.literal("Accent Color"))
                    .description(Component.literal("Color for important elements"))
                    .binding(() -> config.accentColor, v -> config.accentColor = v)
                    .defaultValue(0xFFFF5555)
                    .showAlpha(true)
                )
                
                .separator()
                .label(Component.literal("§lLayout"))
                
                .slider(s -> s
                    .name(Component.literal("UI Scale"))
                    .description(Component.literal("Scale factor for UI elements"))
                    .range(0.5, 2.0, 0.25)
                    .bindingDouble(() -> config.uiScale, v -> config.uiScale = v)
                    .defaultValue(1.0)
                    .percentageFormat()
                )
                
                .<HudPosition>dropdown(d -> d
                    .name(Component.literal("HUD Position"))
                    .description(Component.literal("Where to display the HUD overlay"))
                    .enumClass(HudPosition.class)
                    .binding(() -> config.hudPosition, v -> config.hudPosition = v)
                    .defaultValue(HudPosition.TOP_LEFT)
                )
                
                .textInput(t -> t
                    .name(Component.literal("Custom Title"))
                    .description(Component.literal("Custom text to display in the title bar"))
                    .binding(() -> config.customTitle, v -> config.customTitle = v)
                    .defaultValue("My Mod")
                    .maxLength(32)
                    .placeholder("Enter title...")
                )
            )
            
            // Gameplay Category
            .category(cat -> cat
                .name(Component.literal("Gameplay"))
                .description(Component.literal("Game mechanics and behavior"))
                
                .<GameDifficulty>dropdown(d -> d
                    .name(Component.literal("Difficulty"))
                    .description(Component.literal("Game difficulty level"))
                    .enumClass(GameDifficulty.class)
                    .binding(() -> config.difficulty, v -> config.difficulty = v)
                    .defaultValue(GameDifficulty.NORMAL)
                )
                
                .slider(s -> s
                    .name(Component.literal("Movement Speed"))
                    .description(Component.literal("Player movement speed multiplier"))
                    .range(0.5, 2.0, 0.1)
                    .bindingDouble(() -> config.movementSpeed, v -> config.movementSpeed = v)
                    .defaultValue(1.0)
                    .percentageFormat()
                )
                
                .toggle(t -> t
                    .name(Component.literal("Enable PvP"))
                    .description(Component.literal("Allow player vs player combat"))
                    .binding(() -> config.enablePvp, v -> config.enablePvp = v)
                    .defaultValue(true)
                )
                
                .separator()
                .label(Component.literal("§lAuto-Save"))
                
                .toggle(t -> t
                    .name(Component.literal("Enable Auto-Save"))
                    .description(Component.literal("Automatically save the game periodically"))
                    .binding(() -> config.autoSave, v -> config.autoSave = v)
                    .defaultValue(true)
                )
                
                .slider(s -> s
                    .name(Component.literal("Auto-Save Interval"))
                    .description(Component.literal("Minutes between auto-saves"))
                    .range(1, 30, 1)
                    .bindingInt(() -> config.autoSaveInterval, v -> config.autoSaveInterval = v)
                    .defaultValue(5)
                    .suffix(" min")
                )
                
                .toggle(t -> t
                    .name(Component.literal("Show Tutorials"))
                    .description(Component.literal("Display tutorial popups for new features"))
                    .binding(() -> config.showTutorials, v -> config.showTutorials = v)
                    .defaultValue(true)
                )
            )
            
            .build();
    }
}
