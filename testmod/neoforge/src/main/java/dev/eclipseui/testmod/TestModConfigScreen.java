package dev.eclipseui.testmod;

import dev.eclipseui.EclipseUI;
import dev.eclipseui.api.Theme;
import dev.eclipseui.testmod.TestModConfig.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/**
 * Factory class for creating test config screens demonstrating EclipseUI.
 */
public class TestModConfigScreen {
    
    private static String getThemeName(Theme theme) {
        return switch(theme) {
            case FAITHFUL -> "Faithful Theme";
            case MODERN -> "Modern Theme";
            case CUSTOM -> "Custom Theme";
        };
    }
    
    /**
     * Creates the main config screen with theme switching support.
     */
    public static Screen create(Screen parent) {
        TestModConfig config = TestModConfig.get();
        
        return EclipseUI.configScreen()
            .title(Component.literal("TestMod Config - " + getThemeName(config.theme)))
            .parent(parent)
            .theme(config.theme)
            .onSave(config::save)
            .onReset(config::resetToDefaults)
            
            // Appearance Category
            .category(cat -> cat
                .name(Component.literal("Appearance"))
                .description(Component.literal("Customize the visual appearance"))
                
                .label(Component.literal("§lTheme"))
                
                .<Theme>dropdown(d -> d
                    .name(Component.literal("UI Theme"))
                    .description(Component.literal("Select the visual theme\n§7Changes apply immediately"))
                    .enumClass(Theme.class)
                    .binding(() -> config.theme, v -> config.theme = v)
                    .defaultValue(Theme.MODERN)
                    .formatter(theme -> Component.literal(switch(theme) {
                        case FAITHFUL -> "Faithful";
                        case MODERN -> "Modern";
                        case CUSTOM -> "Custom";
                    }))
                    .onChange(newTheme -> {
                        Minecraft mc = Minecraft.getInstance();
                        mc.setScreen(create(parent));
                    })
                )
                
                .separator()
                .label(Component.literal("§lColor Scheme"))
                
                .colorPicker(c -> c
                    .name(Component.literal("Primary Color"))
                    .description(Component.literal("Main theme color"))
                    .binding(() -> config.primaryColor, v -> config.primaryColor = v)
                    .defaultValue(0xFF5555FF)
                    .allowAlpha(false)
                )
                
                .colorPicker(c -> c
                    .name(Component.literal("Secondary Color"))
                    .description(Component.literal("Accent color for highlights"))
                    .binding(() -> config.secondaryColor, v -> config.secondaryColor = v)
                    .defaultValue(0xFF55FF55)
                    .allowAlpha(false)
                )
                
                .colorPicker(c -> c
                    .name(Component.literal("Accent Color"))
                    .description(Component.literal("Color for important elements"))
                    .binding(() -> config.accentColor, v -> config.accentColor = v)
                    .defaultValue(0xFFFF5555)
                    .allowAlpha(true)
                )
                
                .separator()
                
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
                    .description(Component.literal("Where to display the HUD"))
                    .enumClass(HudPosition.class)
                    .binding(() -> config.hudPosition, v -> config.hudPosition = v)
                    .defaultValue(HudPosition.TOP_LEFT)
                )
                
                .textInput(t -> t
                    .name(Component.literal("Custom Title"))
                    .description(Component.literal("Custom text for title bar"))
                    .binding(() -> config.customTitle, v -> config.customTitle = v)
                    .defaultValue("TestMod")
                    .maxLength(32)
                    .placeholder(Component.literal("Enter title..."))
                )
            )
            
            // General Category
            .category(cat -> cat
                .name(Component.literal("General"))
                .description(Component.literal("Basic mod settings"))
                
                .toggle(t -> t
                    .name(Component.literal("Enable Mod"))
                    .description(Component.literal("Master toggle for all features"))
                    .binding(() -> config.enableMod, v -> config.enableMod = v)
                    .defaultValue(true)
                )
                
                .toggle(t -> t
                    .name(Component.literal("Show Notifications"))
                    .description(Component.literal("Display popup notifications"))
                    .binding(() -> config.showNotifications, v -> config.showNotifications = v)
                    .defaultValue(true)
                )
                
                .separator()
                
                .toggle(t -> t
                    .name(Component.literal("Debug Mode"))
                    .description(Component.literal("Enable debug logging"))
                    .binding(() -> config.debugMode, v -> config.debugMode = v)
                    .defaultValue(false)
                )
                
                .toggle(t -> t
                    .name(Component.literal("Experimental Features"))
                    .description(Component.literal("Enable unstable features\n§cMay cause issues!"))
                    .binding(() -> config.experimentalFeatures, v -> config.experimentalFeatures = v)
                    .defaultValue(false)
                    .requiresRestart(true)
                )
            )
            
            // Graphics Category
            .category(cat -> cat
                .name(Component.literal("Graphics"))
                .description(Component.literal("Visual settings"))
                
                .<GraphicsQuality>dropdown(d -> d
                    .name(Component.literal("Graphics Quality"))
                    .description(Component.literal("Overall quality preset"))
                    .enumClass(GraphicsQuality.class)
                    .binding(() -> config.graphicsQuality, v -> config.graphicsQuality = v)
                    .defaultValue(GraphicsQuality.MEDIUM)
                )
                
                .slider(s -> s
                    .name(Component.literal("Render Distance"))
                    .description(Component.literal("World render distance"))
                    .range(2, 32, 1)
                    .bindingInt(() -> config.renderDistance, v -> config.renderDistance = v)
                    .defaultValue(12)
                    .suffix(" chunks")
                )
                
                .slider(s -> s
                    .name(Component.literal("Brightness"))
                    .description(Component.literal("Screen brightness"))
                    .range(0.0, 2.0, 0.1)
                    .bindingDouble(() -> config.brightness, v -> config.brightness = v)
                    .defaultValue(1.0)
                    .percentageFormat()
                )
                
                .separator()
                
                .toggle(t -> t
                    .name(Component.literal("Enable Shaders"))
                    .description(Component.literal("Shader-based effects"))
                    .binding(() -> config.enableShaders, v -> config.enableShaders = v)
                    .defaultValue(false)
                )
                
                .toggle(t -> t
                    .name(Component.literal("Fancy Particles"))
                    .description(Component.literal("Enhanced particles"))
                    .binding(() -> config.fancyParticles, v -> config.fancyParticles = v)
                    .defaultValue(true)
                )
                
                .slider(s -> s
                    .name(Component.literal("Max FPS"))
                    .description(Component.literal("Frame rate limit"))
                    .range(0, 240, 10)
                    .bindingInt(() -> config.maxFps, v -> config.maxFps = v)
                    .defaultValue(60)
                    .suffix(" FPS")
                )
            )
            
            // Audio Category
            .category(cat -> cat
                .name(Component.literal("Audio"))
                .description(Component.literal("Sound settings"))
                
                .slider(s -> s
                    .name(Component.literal("Master Volume"))
                    .description(Component.literal("Overall volume"))
                    .range(0, 100, 5)
                    .bindingInt(() -> config.masterVolume, v -> config.masterVolume = v)
                    .defaultValue(100)
                    .suffix("%")
                )
                
                .slider(s -> s
                    .name(Component.literal("Music Volume"))
                    .description(Component.literal("Background music"))
                    .range(0, 100, 5)
                    .bindingInt(() -> config.musicVolume, v -> config.musicVolume = v)
                    .defaultValue(80)
                    .suffix("%")
                )
                
                .slider(s -> s
                    .name(Component.literal("Sound Effects"))
                    .description(Component.literal("SFX volume"))
                    .range(0, 100, 5)
                    .bindingInt(() -> config.sfxVolume, v -> config.sfxVolume = v)
                    .defaultValue(100)
                    .suffix("%")
                )
                
                .separator()
                
                .<SoundMode>dropdown(d -> d
                    .name(Component.literal("Sound Mode"))
                    .description(Component.literal("Audio output mode"))
                    .enumClass(SoundMode.class)
                    .binding(() -> config.soundMode, v -> config.soundMode = v)
                    .defaultValue(SoundMode.STEREO)
                )
                
                .toggle(t -> t
                    .name(Component.literal("Mute When Unfocused"))
                    .description(Component.literal("Mute when game loses focus"))
                    .binding(() -> config.muteWhenUnfocused, v -> config.muteWhenUnfocused = v)
                    .defaultValue(false)
                )
            )
            
            .build();
    }
}
