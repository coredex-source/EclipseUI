package dev.eclipseui.fabric;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import dev.eclipseui.EclipseUI;
import dev.eclipseui.api.Theme;
import dev.eclipseui.fabric.example.ExampleConfigScreens;
import net.minecraft.network.chat.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * ModMenu integration for EclipseUI's own config screen.
 * 
 * <p>To integrate your mod with ModMenu using EclipseUI, implement ModMenuApi like this:</p>
 * <pre>{@code
 * public class MyModMenuIntegration implements ModMenuApi {
 *     @Override
 *     public ConfigScreenFactory<?> getModConfigScreenFactory() {
 *         return parent -> EclipseUI.configScreen()
 *             .title(Component.literal("My Mod Config"))
 *             .parent(parent)
 *             .theme(Theme.MODERN)
 *             .category(cat -> cat
 *                 .name(Component.literal("General"))
 *                 // ... add options
 *             )
 *             .build();
 *     }
 * }
 * }</pre>
 */
public class ModMenuIntegration implements ModMenuApi {
    
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        // EclipseUI's own config screen - showcases theme switching with example options
        return parent -> ExampleConfigScreens.createModernScreen(parent);
    }
    
    @Override
    public Map<String, ConfigScreenFactory<?>> getProvidedConfigScreenFactories() {
        // Provide example config screens for demonstration
        // These show up as separate entries if those "mods" were registered
        Map<String, ConfigScreenFactory<?>> factories = new HashMap<>();
        
        // You could register config screens for other mods here if they don't have one
        // factories.put("other-mod-id", parent -> createScreenForOtherMod(parent));
        
        return factories;
    }
}
